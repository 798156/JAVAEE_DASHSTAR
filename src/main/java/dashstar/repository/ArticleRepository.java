package dashstar.repository;

import dashstar.model.Article;
import dashstar.util.HibernateUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

@ApplicationScoped
public class ArticleRepository {

    public List<Article> findAll(int page ,int size) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        List<Article> articles = null;
        try {
            em.getTransaction().begin();
            int start = (page - 1) * size;//添加分页功能，计算查询的起始位置
            articles = em
                    .createQuery("SELECT a FROM Article a", Article.class)
                    .setFirstResult(start)//添加分页功能
                    .setMaxResults(size)//添加分页功能
                    .getResultList();    //添加分页功能
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("articles_not_found", e);
        } finally {
            em.close();
        }
        return articles;
    }

    public Article findByID(Integer id) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        Article article = null;   //添加分页功能
        try {
            em.getTransaction().begin();
            article = em
                    .createQuery("SELECT a FROM Article a WHERE a.id = :id", Article.class)
                    .setParameter("id", id)
                    .getSingleResult();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("article_not_found", e);
        } finally {
            em.close();
        }
        return article;
    }

    public void create(Article article) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(article);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("", e);
        } finally {
            em.close();
        }
    }

    public void update(Article article) throws PersistenceException {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Article existingArticle = em.find(Article.class, article.getId());
            try {
                HibernateUtil.copyNonNullProperties(article, existingArticle);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            em.merge(existingArticle);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("", e);
        } finally {
            em.close();
        }
    }

    public long countAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        return em.createQuery("SELECT COUNT(a) FROM Article a", Long.class).getSingleResult();
    }
}
