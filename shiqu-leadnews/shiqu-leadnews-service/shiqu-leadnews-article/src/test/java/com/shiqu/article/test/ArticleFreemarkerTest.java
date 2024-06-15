package com.shiqu.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shiqu.article.ArticleApplication;
import com.shiqu.article.mapper.ApArticleContentMapper;
import com.shiqu.article.mapper.ApArticleMapper;
import com.shiqu.file.service.FileStorageService;
import com.shiqu.model.article.pojos.ApArticle;
import com.shiqu.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {
    @Autowired
    private ApArticleContentMapper contentMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private Configuration configuration;

    @Autowired
    private ApArticleMapper articleMapper;

    @Autowired
    private DataSource dataSource;

    @Value("${mybatis-plus.mapper-locations}")
    private String mapperLocations;

    @Test
    public void createStaticUrlTest() throws Exception{
        //1.获取文章内容
        ApArticleContent apArticleContent = contentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, 1302977178887004162L));
        String content = apArticleContent.getContent();

        //2.使用freemarker生成静态文件
        Template template = configuration.getTemplate("article.ftl");
        Map<String, Object> params = new HashMap<>();
        params.put("content", JSONArray.parseArray(content));
        StringWriter writer = new StringWriter();
        template.process(params, writer);

        //3.将静态文件上传到minio中
        InputStream is = new ByteArrayInputStream(writer.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", is);

        //4.修改ap_article表，保存static_url字段
        ApArticle apArticle = new ApArticle();
        apArticle.setId(apArticleContent.getArticleId());
        apArticle.setStaticUrl(path);
        articleMapper.updateById(apArticle);
    }

    @Test
    public void createStaticUrlTest2() throws Exception {
        //1.获取文章内容
        ApArticleContent apArticleContent = contentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, 1302865474094120961L));
        if(apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())){
            //2.文章内容通过freemarker生成html文件
            StringWriter out = new StringWriter();
            Template template = configuration.getTemplate("article.ftl");

            Map<String, Object> params = new HashMap<>();
            params.put("content", JSONArray.parseArray(apArticleContent.getContent()));

            template.process(params, out);
            InputStream is = new ByteArrayInputStream(out.toString().getBytes());

            //3.把html文件上传到minio中
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", is);

            //4.修改ap_article表，保存static_url字段
            ApArticle article = new ApArticle();
            article.setId(apArticleContent.getArticleId());
            article.setStaticUrl(path);
            articleMapper.updateById(article);

        }
    }

    @Test
    public void test1() throws SQLException {
        System.out.println(dataSource.getConnection().getClientInfo());
    }
}
