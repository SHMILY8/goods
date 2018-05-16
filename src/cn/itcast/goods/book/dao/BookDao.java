package cn.itcast.goods.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.pager.Expression;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.goods.pager.PageConstants;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * ��bid��ѯ
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public Book findByBid(String bid) throws SQLException {
		String sql = "select * from t_book where bid=?";
		// һ�м�¼�У������˺ܶ��book�����ԣ�����һ��cid����
		Map<String,Object> map = qr.query(sql, new MapHandler(), bid);
		// ��Map�г���cid�������������ӳ�䵽Book������
		Book book = CommonUtils.toBean(map, Book.class);
		// ��Map��cid����ӳ�䵽Category�У������Categoryֻ��cid
		Category category = CommonUtils.toBean(map, Category.class);
		// ���߽�����ϵ
		book.setCategory(category);
		return book;
	}
	
	/**
	 * �������ѯ
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	public PageBean<Book> findByCategory(String cid, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid", "=", cid));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * ������ģ����ѯ
	 * @param bname
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like", "%" + bname + "%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * �����߲�
	 * @param bname
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("author", "like", "%" + author + "%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * ���������
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("press", "like", "%" + press + "%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * ��������ϲ�ѯ
	 * @param combination
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book criteria, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like", "%" + criteria.getBname() + "%"));
		exprList.add(new Expression("author", "like", "%" + criteria.getAuthor() + "%"));
		exprList.add(new Expression("press", "like", "%" + criteria.getPress() + "%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * ͨ�õĲ�ѯ����
	 * @param exprList
	 * @param pc
	 * @return
	 * @throws SQLException 
	 */
	private PageBean<Book> findByCriteria(List<Expression> exprList, int pc) throws SQLException {
		/*
		 * 1. �õ�ps
		 * 2. �õ�tr
		 * 3. �õ�beanList
		 * 4. ����PageBean������
		 */
		/*
		 * 1. �õ�ps
		 */
		int ps = PageConstants.BOOK_PAGE_SIZE;//ÿҳ��¼��
		/*
		 * 2. ͨ��exprList������where�Ӿ�
		 */
		StringBuilder whereSql = new StringBuilder(" where 1=1"); 
		List<Object> params = new ArrayList<Object>();//SQL�����ʺţ����Ƕ�Ӧ�ʺŵ�ֵ
		for(Expression expr : exprList) {
			/*
			 * ����һ�������ϣ�
			 * 1) ��and��ͷ
			 * 2) ����������
			 * 3) �������������������=��!=��>��< ... is null��is nullû��ֵ
			 * 4) �����������is null����׷���ʺţ�Ȼ������params������һ���ʺŶ�Ӧ��ֵ
			 */
			whereSql.append(" and ").append(expr.getName())
				.append(" ").append(expr.getOperator()).append(" ");
			// where 1=1 and bid = ?
			if(!expr.getOperator().equals("is null")) {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}

		/*
		 * 3. �ܼ�¼�� 
		 */
		String sql = "select count(*) from t_book" + whereSql;
		Number number = (Number)qr.query(sql, new ScalarHandler(), params.toArray());
		int tr = number.intValue();//�õ����ܼ�¼��
		/*
		 * 4. �õ�beanList������ǰҳ��¼
		 */
		sql = "select * from t_book" + whereSql + " order by orderBy limit ?,?";
		params.add((pc-1) * ps);//��ǰҳ���м�¼���±�
		params.add(ps);//һ����ѯ���У�����ÿҳ��¼��
		
		List<Book> beanList = qr.query(sql, new BeanListHandler<Book>(Book.class), 
				params.toArray());
		
		/*
		 * 5. ����PageBean�����ò���
		 */
		PageBean<Book> pb = new PageBean<Book>();
		/*
		 * ����PageBeanû��url�����������Servlet���
		 */
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		
		return pb;
	}
	
	public static void main(String[] args) {
		BookDao bookDao = new BookDao();
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bid", "=", "1"));
		exprList.add(new Expression("bname", "like", "%java%"));
		exprList.add(new Expression("edition", "is null", null));
		
//		bookDao.findByCriteria(exprList, 10);
	}
}