package com.floriantoenjes.analyzer.data;

import com.floriantoenjes.analyzer.model.Country;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import java.util.Arrays;
import java.util.List;

public class CountryDao {
    private final SessionFactory sessionFactory = buildSessionFactory();

    private SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    private Session startTransaction() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }

    private void finishTransaction(Session session) {
        session.getTransaction().commit();
        session.close();
    }

    public void save(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::save);
        finishTransaction(session);
    }

    public void update(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::update);
        finishTransaction(session);
    }

    public void delete(Country... countries) {
        Session session = startTransaction();
        Arrays.stream(countries).forEach(session::delete);
        finishTransaction(session);
    }

    public Country getCountryByCode(String code) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class).add(Restrictions.eq("code", code));
        Country country = (Country) criteria.uniqueResult();
        session.close();
        return country;
    }

    @SuppressWarnings("unchecked")
    public List<Country> getCountryList() {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class).addOrder(Order.asc("code"));
        List<Country> countries = criteria.list();
        session.close();
        return countries;
    }
}
