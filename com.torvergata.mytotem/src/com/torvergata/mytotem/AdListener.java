package com.torvergata.mytotem;

import com.google.ads.*;

public interface AdListener {
	  public void onReceiveAd(Ad ad);
	  public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode error);
	  public void onPresentScreen(Ad ad);
	  public void onDismissScreen(Ad ad);
	  public void onLeaveApplication(Ad ad);
	}