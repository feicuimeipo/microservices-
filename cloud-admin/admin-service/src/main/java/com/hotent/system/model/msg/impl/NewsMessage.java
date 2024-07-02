/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.system.model.msg.impl;

import org.nianxi.utils.JsonUtil;
import com.hotent.system.model.msg.BaseMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * news消息
 * <pre>
 * {
   "touser": "UserID1|UserID2|UserID3",
   "toparty": " PartyID1 | PartyID2 ",
   "totag": " TagID1 | TagID2 ",
   "msgtype": "news",
   "agentid": "1",
   "news": {
       "articles":[
           {
               "title": "Title",
               "description": "Description",
               "url": "URL",
               "picurl": "PIC_URL"
           },
           {
               "title": "Title",
               "description": "Description",
               "url": "URL",
               "picurl": "PIC_URL"
           }
       ]
   }
}
 * </pre>
 * @author ray
 *
 */
public class NewsMessage extends BaseMessage {
    private static final Log logger= LogFactory.getLog(NewsMessage.class);
	private List<Article> news=new ArrayList<Article>();

	public String getMsgtype() {
		return "news";
	}

	public List<Article> getNews() {
		return news;
	}

	public void setNews(List<Article> news) {
		this.news = news;
	}

	public void addArticle(Article article){
		this.news.add(article);
	}


	@Override
	public String toString(){
        String json = "{\"touser\": \"%s\",\"toparty\": \"%s\",\"msgtype\": \"news\",\"agentid\": \"%s\",\"news\": {\"articles\":%s}}";

        try {
            String newsJson=JsonUtil.toJson(this.news);
            json=String.format(json, this.getTouser(),this.getToparty(),this.getAgentid(),newsJson);
        }catch (Exception e){
            logger.error(e);
        }
        return json;
	}




	public static void main(String[] args) {
		NewsMessage message=new NewsMessage();
		message.setTouser("zyg");
		message.setAgentid("13");
		Article a1=new Article("通知","通知","http://www.ifeng.com","");
		Article a2=new Article("兄弟我要请假","兄弟我要请假","http://www.163.com","");
		message.addArticle(a1);
		message.addArticle(a2);
		System.out.println(message);
	}
}
