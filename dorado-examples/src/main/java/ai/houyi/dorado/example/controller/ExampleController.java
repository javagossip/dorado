/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package ai.houyi.dorado.example.controller;

import ai.houyi.dorado.example.controller.helper.MyException;
import ai.houyi.dorado.example.model.Campaign;
import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.DELETE;
import ai.houyi.dorado.rest.annotation.GET;
import ai.houyi.dorado.rest.annotation.POST;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.annotation.RequestParam;
import io.swagger.annotations.Api;

/**
 * @author wangweiping
 *
 */
@Controller
@Path("/campaign")
@Api(tags="推广活动管理")
public class ExampleController {
	@Path("/{id:[0-9]+}")
	@GET
	public Campaign newCampaign(int id) {
		Campaign campaign = new Campaign();
		campaign.setId(id);
		campaign.setName("test campaign");

		return campaign;
	}

	@GET
	@Path("/qtest")
	public String queryTest(@RequestParam("q") String query) {
		return "QQ";
	}
	
	@Path("/name/{name}")
	public String campaignName(String name) {
		return String.format("hello_campaign, %s", name);
	}

	@POST
	public Campaign save(Campaign campaign) {
		System.out.println(campaign);
		return campaign;
	}

	@Path("/{id}")
	@DELETE
	public void deleteCampaign(int id) {
		System.out.println("delete campaign, id: " + id);
	}

	@GET
	@Path("/{id}")
	public Campaign getCampaign(int id) {
		return Campaign.builder().withId(12).withName("网易考拉推广计划").build();
	}
	
	@GET
	@Path("/exception/my")
	public Campaign testMyException(int id) throws MyException {
		throw new MyException("my exception");
	}

	@GET
	@Path("/exception/default")
	public Campaign testDefaultException(int id) throws Exception {
		throw new Exception("default exception");
	}
}
