package formulas;

import java.util.*;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.*;
import javax.persistence.criteria.*;

public class FormulaManager { 
	private static SessionFactory factory = null;
	private static FormulaManager fm = null;
    public static FormulaManager initList()
    {
        /*try {*/
            if (factory == null)
				factory = new Configuration().configure().buildSessionFactory();
        /*} catch (Throwable ex) { 
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex); 
        }*/
        if (fm == null)
        	fm = new FormulaManager();
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
    public void updateFormula(Integer formula_id, String TeX, Integer pageNumber, List<Integer> symbols)
    {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Set<Symbol> symb = new HashSet<Symbol>();
    		for (Integer symb_id: symbols) {
    			symb.add(session.get(Symbol.class, symb_id));
    		}
    		Formula formula = session.get(Formula.class, formula_id);
    		formula.setFormulaTex(TeX);
    		formula.setPageNum(pageNumber);
    		formula.setSymbols(symb);
    		//formula.setResultSymbol(null);
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
    		//f.setResultSymbol(null);
    		System.out.println("Saving formula with TeX - " + f_tex + " and page - " + f_pn);
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

	public List<Symbol> find_symbol(String s) {
		Session session = factory.openSession();
		CriteriaBuilder cb_symbols = session.getCriteriaBuilder();
		CriteriaQuery<Symbol> cquery = cb_symbols.createQuery(Symbol.class);
		Root<Symbol> root = cquery.from(Symbol.class);
		cquery.select(root).where(cb_symbols.equal(root.get("symbolTex").as(String.class), s));
		
		Query<Symbol> qnative = session.createQuery(cquery);
		List<Symbol> ci_result = qnative.getResultList();
		List<Symbol> cs_result = new ArrayList<Symbol>();
		for (Symbol item : ci_result) {
			if (item.getSymbolTex().compareTo(s) == 0)
				cs_result.add(item);
		}
		return cs_result;
	}
	
    public Integer insertSymbol(String s_tex) {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	Integer new_id = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Symbol s = new Symbol();
    		s.setSymbolTex(s_tex);
    		new_id = (Integer)session.save(s);
    		tx.commit();
    	} catch (HibernateException e) {
    		if (tx != null)
    			tx.rollback();
    		e.printStackTrace();
    	} finally {
    		session.close();
    	}
		return new_id;
    }
    
    public Integer insertSymbol(String s_tex, List<Formula> result_list) {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	Integer new_id = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Symbol s = new Symbol();
    		s.setSymbolTex(s_tex);
    		s.setFormulas(result_list);
    		new_id = (Integer)session.save(s);
    		tx.commit();
    	} catch (HibernateException e) {
    		if (tx != null)
    			tx.rollback();
    		e.printStackTrace();
    	} finally {
    		session.close();
    	}
		return new_id;
    }

    
    public void updateSymbol(Integer SymbolId, Integer formula_id)
    {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Symbol symbol = session.get(Symbol.class, SymbolId);
    		Formula formula = session.get(Formula.class, formula_id);
    		ArrayList<Formula> ar_f = new ArrayList<Formula>();
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
    
    public void updateSymbol(Integer SymbolId, List<Formula> formula_results)
    {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try {
    		tx = session.beginTransaction();
    		Symbol symbol = session.get(Symbol.class, SymbolId);
    		symbol.setFormulas(formula_results);
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
    public List listFormulas(String TeX)
    {
    	Session session = factory.openSession();
    	Transaction tx = null;
    	
    	try
    	{
    		tx = session.beginTransaction();
    		System.out.println("Searching for " + TeX);
    		List formulas =	session.createQuery("FROM Formula WHERE formulaTex LIKE '%" + TeX + "%' ORDER BY pageNum ASC").list();

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
