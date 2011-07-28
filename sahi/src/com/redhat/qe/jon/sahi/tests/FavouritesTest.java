package com.redhat.qe.jon.sahi.tests;
import org.testng.annotations.*;
import com.redhat.qe.jon.sahi.base.SahiTestScript;

public class FavouritesTest extends SahiTestScript {
	
	@Test(groups="FavouriteTest")
	public void createFavourite(){
		 sahiTasks.createFavourite();
	}
	
	@Test(groups="FavouriteTest",dependsOnMethods ={"createFavourite"})
	public void removeFavourite(){
		
		sahiTasks.removeFavourite();
	}
	@Test(groups="FavouriteTest",dependsOnMethods ={"createFavourite"})
	public void checkFavouriteBadgeForAgent(){
		sahiTasks.checkFavouriteBadgeForAgent();
		
	}
	@Test(groups="FavouriteTest", dependsOnMethods = {"removeFavourite"})
	public void checkBadgeAfterRmovingFavourite(){
		sahiTasks.checkBadgeAfterRmovingFavourite();
	}

}
