package it.thecrawlers.persistence;

import it.thecrawlers.persistence.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
/**
 * Classe che modella le operazioni di base per l'accesso ai dati (leggi, modifica, trova tutti,
 * trova quelli che rispettano un certo criterio).
 * @author Pierpaolo Paris
 *
 * @param <T> Il tipo degli oggetti da recuperare
 */
public class DAO<T>{
	private Class<? extends T> model;
	/**
	 * Costruttore vuoto
	 */
	public DAO(){
		super();
	}
	/**
	 * Costruisce un nuovo oggetto DAO per la classe specificata
	 * @param model
	 */
	public DAO(Class<? extends T> model) {
		this();
		this.model = model;
	}
	@SuppressWarnings("unchecked")
	/**
	 * Restituisce tutti gli oggetti di tipi T salvati
	 * @return la lista degli oggetti
	 */
	public List<T> list() {
		return (List<T>) this.list((Criteria)null);
	}
	@SuppressWarnings("unchecked")
	/**
	 * Restituisce tutti gli oggetti di tipo T salvati che rispettano il
	 * Criterio c
	 * @return la lista degli oggetti
	 * @param c il Criteria
	 */
	public List<? extends T> list(Criteria c){
		Session sessione = null;
		List<? extends T> result = null;
		try{
			sessione = HibernateUtil.getSession();
			if(c == null) c = sessione.createCriteria(this.model);
			result = (List<? extends T>)c.list();
			sessione.close();
		}
		catch(HibernateException he){
			he.printStackTrace();
			sessione.close();
		}

		if(result == null) result = new ArrayList<T>();
		return result;    
	}

	/**
	 * Restituisce l'oggetto di tipo t che ha l'id specificato
	 *
	 * @param id l'id
	 * @return l'oggetto
	 */
	 @SuppressWarnings("unchecked")
	public T get(Object id){
		 Session sessione = null;
		 T result = null;
		 try{
			 sessione = HibernateUtil.getSession();
			 result = (T)sessione.createCriteria(this.model).add(Restrictions.eq("id",id)).uniqueResult();
			 sessione.close();
		 }
		 catch(HibernateException he){
			 he.printStackTrace();
			 sessione.close();
		 }
		 return result;
	 }
	 /**
	  * Rimuove l'oggetto che ha quell'id
	  * @param id l'id dell'oggetto
	  */
	 public void removeById(Object id){
		 Session sessione=null;
		 Transaction transazione=null;
		 T oggetto=this.get(id);
		 try{
			 sessione=HibernateUtil.getSession();
			 transazione=sessione.beginTransaction();
			 sessione.delete(oggetto);
			 transazione.commit();
		 }
		 catch(HibernateException he){
			 System.out.println("Eccezione catturata dal metodo removeById di DAO");
			 transazione.rollback();
		 }
		 finally{
			 sessione.close();
		 }
	 }
	 /**
	  * Salva un nuovo oggetto (o ne modifica uno)
	  * @param object l'oggetto da salvare (o da modificare)
	  */
	 public void set(T object) {
		 Session sessione=null;
		 Transaction transazione=null;
		 try{
			 sessione=HibernateUtil.getSession();
			 transazione=sessione.beginTransaction();
			 sessione.saveOrUpdate(object);
			 transazione.commit();
			 sessione.refresh(object);
		 }
		 catch(HibernateException he){
			 he.printStackTrace();
			 transazione.rollback();     
		 }
		 sessione.close();    
	 }  
}
