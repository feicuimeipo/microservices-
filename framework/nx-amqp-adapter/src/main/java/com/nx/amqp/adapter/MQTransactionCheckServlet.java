package com.nx.amqp.adapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class MQTransactionCheckServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String URI = "/mqtc/checker";

	private TransactionChecker transactionChecker;

	public MQTransactionCheckServlet(TransactionChecker transactionChecker) {
		super();
		this.transactionChecker = transactionChecker;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean status;
		if(transactionChecker == null){
			status = true;
		}else{
			status = transactionChecker.check(request.getParameter(TransactionChecker.TRANSACTION_PARAM_NAME));
		}

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.append(String.valueOf(status));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}