package dao;

import config.HibernateUtil;
import model.User;
import org.hibernate.Session;

import java.util.List;

public class UserDAO extends GenericDAO<User> {
    public UserDAO() {
        super(User.class);
    }

    public List<User> findAllWithItems() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.items",
                    User.class
            ).list();
        }
    }
}