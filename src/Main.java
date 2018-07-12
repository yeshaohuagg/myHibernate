import com.yesir.Employee;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.query.Query;
import org.hibernate.cfg.Configuration;

import javax.persistence.metamodel.EntityType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Main {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        final Session session = getSession();
        Main main = new Main();
//        main.addEmploee("小不点","云彩",200);
        try {
            System.out.println("querying all the managed entities...");
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    Employee employee = (Employee) o;
                    System.out.println(employee.getId()+"  " + employee.getFirstName() + "."+employee.getLastName() +"=="+employee.getSalary());
                }
            }
        } finally {
            session.close();
        }
        main.countEmployee();
    }

    public Integer addEmploee(String fname,String lname,int salary){
        Session session = getSession();
        Transaction transaction = null;
        Integer emploeeID= null;
        try {
            transaction = session.beginTransaction();
            for (int i = 0;i<100000;i++){
                Employee employee = new Employee(fname,lname,salary);
                emploeeID = (Integer) session.save(employee);
                if (i % 50 == 0){
                    session.flush();
                    session.clear();
                }
            }

            transaction.commit();
        } catch (HibernateException e){
            if (transaction != null){
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }

        return emploeeID;
    }

    public void updateEmployee(Integer employeeID,int salary){
        Session session = ourSessionFactory.openSession();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            Employee employee = session.get(Employee.class, employeeID);
            employee.setSalary(salary);
            session.update(employee);
            transaction.commit();
        }catch (HibernateException e){
            if (transaction != null){
                transaction.rollback();
            }
        } finally {
            session.close();
        }

    }

    public void deleteEmployee(Integer employeeID){
        Session session = ourSessionFactory.openSession();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            Employee employee = session.get(Employee.class, employeeID);
            session.delete(employee);
            transaction.commit();
        }catch (HibernateException e){
            if (transaction != null){
                transaction.rollback();
            }
        } finally {
            session.close();
        }

    }
    public void countEmployee(){
        Session session = ourSessionFactory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Employee.class);

            // To get total row count.
            cr.setProjection(Projections.rowCount());
            List list = cr.list();

            System.out.println("Total Coint: " + list.get(0) );
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
    }
}