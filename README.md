### 项目名称：识趣新闻
由原黑马头条改动而来，增加了协同过滤算法中的皮尔森相关系数，
## 若需要使用，要准备以下资源：
1. 需要原教程的虚拟机资源
2. nginx反向代理服务器
3. 前端页面资源
4. 数据库修改，新增ap_user_article表
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ap_user_article
-- ----------------------------
DROP TABLE IF EXISTS `ap_user_article`;
CREATE TABLE `ap_user_article`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户id',
  `channel_id` int NULL DEFAULT NULL COMMENT '频道id',
  `score` bigint NULL DEFAULT 0 COMMENT '频道分值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1786976534041485314 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

上述资源完备后，还需要进行nacos以及此项目内部yml文件的配置修改，匹配自己的配置即可
