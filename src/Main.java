import com.yesir.Employee;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.hibernate.cfg.Configuration;

import javax.persistence.metamodel.EntityType;

import java.io.Serializable;
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
        main.addEmploee("你好","nnibni",200);
        main.addEmploee("gggggg","ggggg",2300);
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
    }

    public Integer addEmploee(String fname,String lname,int salary){
        Session session = getSession();
        Transaction transaction = null;
        Integer emploeeID= null;
        try {
            transaction = session.beginTransaction();
            Employee employee = new Employee(fname,lname,salary);
            emploeeID = (Integer) session.save(employee);
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
}