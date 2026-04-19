package dao;

import config.HibernateUtil;
import model.Item;
import org.hibernate.Session;

import java.util.List;

public class ItemDAO extends GenericDAO<Item> {
    public ItemDAO() {
        super(Item.class);
    }

    public List<Item> findAllWithSeller() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT i FROM Item i JOIN FETCH i.seller",
                    Item.class
            ).list();
        }
    }
}