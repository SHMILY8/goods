package cn.itcast.goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

/**
 * �û�ģ��ҵ���
 * @author qdmmy6
 *
 */
public class UserService {
	private UserDao userDao = new UserDao();
	
	//�û���ע��У��
	public boolean ajaxValidateLoginname(String loginname){
		try {
			 return  userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
	}
//����У��
	public boolean ajaxValidateEmail(String email) {
		try {
			 return  userDao.ajaxValidateLoginname(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	
	}
	/**
	 * ע�Ṧ��
	 * @param user
	 */
	public void regist(User user) {
		/*
		 * 1. ���ݵĲ���
		 */
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
		/*
		 * 2. �����ݿ����
		 */
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		/*
		 * 3. ���ʼ�
		 */
		/*
		 * �������ļ����ݼ��ص�prop��
		 */
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		/*
		 * ��¼�ʼ����������õ�session
		 */
		String host = prop.getProperty("host");//������������
		String name = prop.getProperty("username");//��¼��
		String pass = prop.getProperty("password");//��¼����
		Session session = MailUtils.createSession(host, name, pass);
		
		/*
		 * ����Mail����
		 */
		String from = prop.getProperty("from");
		String to = user.getEmail();
		String subject = prop.getProperty("subject");
		// MessageForm.format������ѵ�һ�������е�{0},ʹ�õڶ����������滻��
		// ����MessageFormat.format("���{0}, ��{1}!", "����", "ȥ����"); ���ء������������ȥ���ɣ���
		String content = MessageFormat.format(prop.getProperty("content"), user.getActivationCode());
		Mail mail = new Mail(from, to, subject, content);
		/*
		 * �����ʼ�
		 */
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	//�����
	public void activation(String code) throws UserException{
		
		try {
			User user = userDao.findByCode(code);
			if(user==null) throw new UserException("��Ч�ļ�����");
			if(user.isStatus())throw new UserException(" ���Ѿ��������");
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	
		}
	
	
	/**
	 * ��¼����
	 * @param user
	 * @return
	 */
	public User login(User user) {
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

//�޸�����
	public void updatePassword(String uid,String newpass,String oldpass) throws UserException{
		try {
			boolean bool=userDao.findByUidAndPassword(uid, oldpass);
			if(!bool){
				throw new UserException("���������");
			}
			userDao.updatePassword(uid, newpass);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	
	

}
