/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;



import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pharmcube.mybatis.support.query.PageBean;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryField;
import com.pharmcube.mybatis.support.query.QueryFilter;
import org.nianxi.id.UniqueIdUtil;
import com.hotent.portal.PortalTestCase;
import com.hotent.portal.model.SysIndexLayout;

/**
 * @company 广州宏天软件股份有限公司
 * @author maoww
 * @email maoww@jee-soft.cn
 * @date 2018年6月20日
 */
public class SysIndexLayoutManagerTest extends PortalTestCase{
	@Resource
	SysIndexLayoutManager sil;

	@Test
	public void testCRUD() {
		Long suid = UniqueIdUtil.getUId();

		String alias = "student";
		SysIndexLayout sdm = new SysIndexLayout();
		sdm.setId(suid);
		sdm.setName(alias);

		// 添加
		sil.create(sdm);
		// 查询
		SysIndexLayout bodef = sil.get(suid);
		assertEquals(alias, bodef.getName());

		String newDesc = "新的学生信息";
		bodef.setName(newDesc);
		// 更新
		sil.update(bodef);

		SysIndexLayout bd = sil.get(suid);
		assertEquals(newDesc, bd.getName());

		long suid2 = UniqueIdUtil.getUId();
		String name = "我的首页";
		SysIndexLayout st = new SysIndexLayout();
		st.setId(suid2);
		st.setName(name);

		long suid3 = UniqueIdUtil.getUId();
		String newName = "首页";
		SysIndexLayout stm = new SysIndexLayout();
		stm.setId(suid3);
		stm.setName(newName);

		sil.create(st);
		sil.create(stm);

		// 通过ID列表批量查询
		List<SysIndexLayout> byIds = sil.listByIds(Arrays.asList(new Long[] { suid2, suid3 }));
		assertEquals(2, byIds.size());

		QueryFilter queryFilter = QueryFilter.build().withDefaultPage().withQuery(new QueryField("name", newDesc));
		// 通过通用查询条件查询
		PageList<SysIndexLayout> query = sil.query(queryFilter);
		assertEquals(1, query.getTotal());

		// 通过指定列查询唯一记录
		SysIndexLayout bab = sil.getOne(Wrappers.<SysIndexLayout>lambdaQuery().eq(SysIndexLayout::getName, newDesc));
		assertEquals(newDesc, bab.getName());

		// 查询所有数据
		List<SysIndexLayout> all = sil.list();
		assertEquals(3, all.size());

		// 查询所有数据(分页)
		PageList<SysIndexLayout> allByPage = sil.page(new PageBean(1, 2));
		assertEquals(3, allByPage.getTotal());
		assertEquals(2, allByPage.getRows().size());

		// 通过ID删除数据
		sil.remove(suid);
		SysIndexLayout sstd = sil.getOne(Wrappers.<SysIndexLayout>lambdaQuery().eq(SysIndexLayout::getName, newDesc));
		assertTrue(sstd == null);

		// 通过ID集合批量删除数据
		sil.removeByIds(Arrays.asList(new Long[] { suid, suid2, suid3 }));
		List<SysIndexLayout> nowAll = sil.list();
		assertEquals(0, nowAll.size());
	}
}
