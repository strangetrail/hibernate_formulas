package formulas;

import java.util.*;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class FormulaManager { 
	private static SessionFactory factory;
    public static FormulaManager initList()
    {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) { 
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex); 
        }
        FormulaManager fm = new FormulaManager();
        //HashSet set1 = new HashSet();
        //set1.add(new Symbol("a"));
        //set1.add(new Symbol("B"));
        //set1.add(new Symbol("c"));
        
        //Integer f01 = fm.AddFormula("B=ac", 234, set1);
        
        //return fm.listFormulas();
        return fm;
        
        //fm.updateSymbol(1, 2);
    }
    
    public Integer AddFormula(String formula_tex, Integer page_num, HashSet symbols) {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	Integer formula_id = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Formula formula = new Formula(formula_tex, page_num);
    		formula.setSymbols(symbols);
    		formula_id = (Integer) session.save(formula);
    		tx.commit();
    	} catch (HibernateException e) {
    		if (tx != null)
    			tx.rollback();
    		e.printStackTrace();
    	} finally {
    		session.close();
    	}
    	return formula_id;
    }

    public void updateFormula(Integer formula_id, String TeX, Integer pageNumber)
    {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Formula formula = session.get(Formula.class, formula_id);
    		formula.setFormulaTex(TeX);
    		formula.setPageNum(pageNumber);
    		session.update(formula);
    		tx.commit();
    	} catch (HibernateException he) {
    		if (tx != null)
    			tx.rollback();
    		he.printStackTrace();
    	} finally {
    		session.close();
    	}

    }

    public void insertFormula(String f_tex, Integer f_pn) {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Formula f = new Formula();
    		f.setFormulaTex(f_tex);
    		f.setPageNum(f_pn);
    		session.save(f);
    		tx.commit();
    	} catch (HibernateException e) {
    		if (tx != null)
    			tx.rollback();
    		e.printStackTrace();
    	} finally {
    		session.close();
    	}
    	
    }

    
    public void deleteFormula(Integer formula_id) {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Formula f = session.get(Formula.class, formula_id);
    		session.delete(f);
    		tx.commit();
    	} catch (HibernateException e) {
    		if (tx != null)
    			tx.rollback();
    		e.printStackTrace();
    	} finally {
    		session.close();
    	}
    	
    }
    
    public void updateSymbol(Integer SymbolId, Integer formula_id)
    {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Symbol symbol = session.get(Symbol.class, SymbolId);
    		Formula formula = session.get(Formula.class, formula_id);
    		ArrayList ar_f = new ArrayList();
    		ar_f.add(formula);
    		symbol.setFormulas(ar_f);
    		session.update(symbol);
    		tx.commit();
    	} catch (HibernateException e) {
    		if (tx != null)
    			tx.rollback();
    		e.printStackTrace();
    	} finally {
    		session.close();
    	}
    }
    
    public List listFormulas()
    {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try
    	{
    		tx = session.beginTransaction();
    		List formulas =	session.createQuery("FROM Formula ORDER BY pageNum ASC").list();

    		//for (Iterator iter1 = formulas.iterator(); iter1.hasNext(); ) {
    		/*for (Object item: formulas) {
    			//Formula formula = (Formula) iter1.next();
    			Formula formula = (Formula) item;
    			System.out.print("TeX: " + formula.getFormulaTex());
    			System.out.println(" Page: " + formula.getPageNum());
    			Set symbols = formula.getSymbols();
        		for (Object item2: symbols) {
        			Symbol symbol = (Symbol) item2;
        			System.out.println("Symbol TeX: " + symbol.getSymbolTex());
        		}
    		}*/
    		tx.commit();
    		return formulas;
    	} catch (HibernateException e)
    	{
    		if (tx != null)
    			tx.rollback();
    		e.printStackTrace();
    	}
    	finally
    	{
    		session.close();
    	}
    	return null;
    }

}
