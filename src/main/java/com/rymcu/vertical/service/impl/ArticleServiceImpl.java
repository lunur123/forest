package com.rymcu.vertical.service.impl;

import com.rymcu.vertical.core.service.AbstractService;
import com.rymcu.vertical.dto.ArticleDTO;
import com.rymcu.vertical.dto.Author;
import com.rymcu.vertical.entity.Article;
import com.rymcu.vertical.entity.ArticleContent;
import com.rymcu.vertical.mapper.ArticleMapper;
import com.rymcu.vertical.service.ArticleService;
import com.rymcu.vertical.util.Html2TextUtil;
import com.rymcu.vertical.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl extends AbstractService<Article> implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    private static final String DOMAIN = "https://rymcu.com";

    @Override
    public List<ArticleDTO> articles(String searchText, String tag) {
        List<ArticleDTO> list = articleMapper.selectArticles(searchText, tag);
        list.forEach(article->{
            article = genArticle(article,0);
        });
        return list;
    }

    @Override
    @Transactional
    public Map postArticle(Integer idArticle, String articleTitle, String articleContent, String articleContentHtml, String articleTags, HttpServletRequest request) {
        Map map = new HashMap();
        Article article;
        if(idArticle == null || idArticle == 0){
            article = new Article();
            article.setArticleTitle(articleTitle);
            article.setArticleAuthorId(5);
            article.setArticleTags(articleTags);
            article.setCreatedTime(new Date());
            article.setUpdatedTime(article.getCreatedTime());
            articleMapper.insertSelective(article);
            article.setArticlePermalink(DOMAIN + "/article/"+article.getIdArticle());
            article.setArticleLink("/article/"+article.getIdArticle());
            articleMapper.insertArticleContent(article.getIdArticle(),articleContent,articleContentHtml);
        } else {
            article = articleMapper.selectByPrimaryKey(idArticle);
            article.setArticleTitle(articleTitle);
            article.setArticleTags(articleTags);
            if(StringUtils.isNotBlank(articleContentHtml)){
                Integer length = articleContentHtml.length();
                if(length>200){
                    length = 200;
                }
                String articlePreviewContent = articleContentHtml.substring(0,length);
                article.setArticlePreviewContent(Html2TextUtil.getContent(articlePreviewContent));
            }
            article.setUpdatedTime(new Date());
            articleMapper.updateArticleContent(article.getIdArticle(),articleContent,articleContentHtml);
        }
        articleMapper.updateByPrimaryKeySelective(article);
        map.put("id", article.getIdArticle());
        return map;
    }

    @Override
    public ArticleDTO findArticleDTOById(Integer id) {
        ArticleDTO articleDTO = articleMapper.selectArticleDTOById(id);
        articleDTO = genArticle(articleDTO,1);
        return articleDTO;
    }

    private ArticleDTO genArticle(ArticleDTO article,Integer type) {
        Author author = articleMapper.selectAuthor(article.getArticleAuthorId());
        article.setArticleAuthor(author);
        article.setTimeAgo(Utils.getTimeAgo(article.getUpdatedTime()));
        if(type == 1){
            ArticleContent articleContent = articleMapper.selectArticleContent(article.getIdArticle());
            article.setArticleContent(articleContent.getArticleContentHtml());
        } else {
          if(StringUtils.isBlank(article.getArticlePreviewContent())){
              ArticleContent articleContent = articleMapper.selectArticleContent(article.getIdArticle());
              Integer length = articleContent.getArticleContentHtml().length();
              if(length>200){
                  length = 200;
              }
              String articlePreviewContent = articleContent.getArticleContentHtml().substring(0,length);
              article.setArticlePreviewContent(Html2TextUtil.getContent(articlePreviewContent));
          }
        }
        return article;
    }
}
