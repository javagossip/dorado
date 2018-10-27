/*
 * Copyright 2014-2018 f2time.com All right reserved.
 */
package mobi.f2time.dorado.example.controller;

import mobi.f2time.dorado.example.model.Campaign;
import mobi.f2time.dorado.rest.annotation.Controller;
import mobi.f2time.dorado.rest.annotation.DELETE;
import mobi.f2time.dorado.rest.annotation.GET;
import mobi.f2time.dorado.rest.annotation.POST;
import mobi.f2time.dorado.rest.annotation.Path;

/**
 * @author wangweiping
 *
 */
@Controller
@Path("/campaign")
public class ExampleController {
	@Path("/{id:[0-9]+}")
	@GET
	public Campaign newCampaign(int id) {
		Campaign campaign = new Campaign();
		campaign.setId(id);
		campaign.setName("test campaign");

		return campaign;
	}

	@Path("/name")
	public String campaignName() {
		return String.format("hello_campaign", "");
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
}
