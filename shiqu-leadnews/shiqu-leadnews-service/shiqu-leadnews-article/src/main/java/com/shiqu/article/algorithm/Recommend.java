package com.shiqu.article.algorithm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiqu.article.service.ApUserArticleService;
import com.shiqu.model.article.pojos.ApUserArticle;
import com.shiqu.model.article.vos.UserArticleStorageVo;
import com.shiqu.model.article.vos.UserArticleVo;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Recommend {
    
    @Autowired
    private ApUserArticleService apUserArticleService;
    
    /**
     * 在给定userId的情况下，计算其他用户和它的距离并排序
     * @param userId
     * @return
     */
    public Map<Double, Integer> computeNearestNeighbor(Integer userId, List<UserArticleStorageVo> userArticleStorageVos) {
        Map<Double, Integer> distances = new TreeMap<>();

        UserArticleStorageVo u1 = new UserArticleStorageVo();
        for (UserArticleStorageVo userArticle : userArticleStorageVos) {
            if (userId.equals(userArticle.getUserId().intValue())) {
                u1 = userArticle;
            }
        }

        for (int i = 0; i < userArticleStorageVos.size(); i++) {
            UserArticleStorageVo u2 = userArticleStorageVos.get(i);
            Integer userId2 = u2.getUserId().intValue();
            if (!userId.equals(userId2)) {

                double distance = pearson_dis(u2.getUserArticleVos(), u1.getUserArticleVos());
                distances.put(distance, u2.getUserId().intValue());
            }

        }
        System.out.println("该用户与其他用户的皮尔森相关系数 -> " + distances);
        return distances;
    }

    public List<UserArticleVo> getUserArticleVoList(Integer userId) throws InvocationTargetException, IllegalAccessException {
        //根据userId取出ApUserArticle列表
        LambdaQueryWrapper<ApUserArticle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApUserArticle::getUserId, userId);
        List<ApUserArticle> apUserArticles = apUserArticleService.list(queryWrapper);
        List<UserArticleVo> userArticleVos = new ArrayList<>();
        //赋值
        for (ApUserArticle userArticle : apUserArticles){
            UserArticleVo userArticleVo = new UserArticleVo();
            BeanUtils.copyProperties(userArticle, userArticleVo);
            userArticleVos.add(userArticleVo);
        }
        return userArticleVos;
    }

    /**
     * 计算2个打分序列间的pearson距离
     * 选择公式四进行计算
     * @param rating1
     * @param rating2
     * @return
     */
    public double pearson_dis(List<UserArticleVo> rating1, List<UserArticleVo> rating2) {
        int n=rating1.size();
        List<Long> rating1ScoreCollect = rating1.stream().map(A -> A.getScore()).collect(Collectors.toList());
        List<Long> rating2ScoreCollect = rating2.stream().map(A -> A.getScore()).collect(Collectors.toList());

        if(rating1ScoreCollect.size()>rating2ScoreCollect.size()){
            int i1 = rating1ScoreCollect.size() - rating2ScoreCollect.size();
            for (int i = 0; i < i1; i++) {
                rating2ScoreCollect.add(0L);
            }
        }else if(rating1ScoreCollect.size()<rating2ScoreCollect.size()){
            int i1 = rating2ScoreCollect.size() - rating1ScoreCollect.size();
            for (int i = 0; i < i1; i++) {
                rating1ScoreCollect.add(0L);
            }
        }

        double Ex= rating1ScoreCollect.stream().mapToDouble(x->x).sum();
        double Ey= rating2ScoreCollect.stream().mapToDouble(y->y).sum();
        double Ex2=rating1ScoreCollect.stream().mapToDouble(x->Math.pow(x,2)).sum();
        double Ey2=rating2ScoreCollect.stream().mapToDouble(y->Math.pow(y,2)).sum();
        double Exy= IntStream.range(0,n).mapToDouble(i->rating1ScoreCollect.get(i)*rating2ScoreCollect.get(i)).sum();
        double numerator=Exy-Ex*Ey/n;
        double denominator=Math.sqrt((Ex2-Math.pow(Ex,2)/n)*(Ey2-Math.pow(Ey,2)/n));
        if (denominator==0) return 0.0;
        return numerator/denominator;
    }


    public List<UserArticleVo> recommend(Integer userId, List<UserArticleStorageVo> userArticleStorageVos){
        //找到最近邻
        Map<Double, Integer> distances = computeNearestNeighbor(userId, userArticleStorageVos);
        UserArticleStorageVo userArticleStorageVo = new UserArticleStorageVo();
        Collection<Integer> values = distances.values();
        Integer nearest = Collections.min(values);
        if (nearest != null){
            userArticleStorageVo.setUserId(nearest.longValue());
        }
        System.out.println("最近邻 -> " + nearest);

        //找到最近邻看过，但是我们没看过的频道，计算推荐
        UserArticleStorageVo neighborRatings = new UserArticleStorageVo();
        for (UserArticleStorageVo user:userArticleStorageVos) {
            assert nearest != null;
            Long near = nearest.longValue();
            if (near.equals(user.getUserId())) {
                neighborRatings = user;
            }
        }
        List<UserArticleVo> userArticleVos = neighborRatings.getUserArticleVos();
        System.out.println("最近邻感兴趣的频道 -> " + userArticleVos);

        UserArticleStorageVo userRatings = new UserArticleStorageVo();
        for (UserArticleStorageVo user:userArticleStorageVos) {
            if (userId.equals(user.getUserId().intValue())) {
                userRatings = user;
            }
        }

        //根据自己和邻居的频道计算推荐的频道
        List<UserArticleVo> recommendationMovies = new ArrayList<>();

        for (UserArticleVo userArticleVo : neighborRatings.getUserArticleVos()) {

            if (userRatings.find(userArticleVo.getChannelId()) == null) {
                recommendationMovies.add(userArticleVo);
            }
        }
        Collections.sort(recommendationMovies);
        return recommendationMovies;
    }
}
