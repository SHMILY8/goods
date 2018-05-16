package cn.itcast.goods.category.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.goods.category.domain.Category;
import cn.itcast.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

/**
 * ����ģ��WEB��
 * @author qdmmy6
 *
 */
public class CategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();	
	
	/**
	 * ��ѯ���з���
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. ͨ��service�õ����еķ���
		 * 2. ���浽request�У�ת����left.jsp
		 */
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return"f:/jsps/left.jsp";
	}
 }
