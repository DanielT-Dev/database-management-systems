package dao;

import config.HibernateUtil;
import model.Transaction;
import org.hibernate.Session;

import java.util.List;

public class TransactionDAO extends GenericDAO<Transaction> {
    public TransactionDAO() {
        super(Transaction.class);
    }

    public List<Transaction> findAllWithRelations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT t FROM Transaction t " +
                            "JOIN FETCH t.buyer " +
                            "JOIN FETCH t.item",
                    Transaction.class
            ).list();
        }
    }
}