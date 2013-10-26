package com.team5hinf.support;

import android.content.Context;
import android.net.Uri;
import edu.neu.android.wocketslib.Globals;

/**
 * The place for redefining/reset some global variables in the WocketsLib.
 *
 * @author bigbug
 */
public class DemoGlobals {

	public static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	public static final int REQUEST_AUTHORIZATION        = 1;
	public static final int REQUEST_ACCOUNT_PICKER       = 2;	
	
	public static final String KEY_ATTACHED_IMAGE_URL = "KEY_ATTACHED_IMAGE_URL";
	public static final String KEY_ATTACHED_VIDEO_URL = "KEY_ATTACHED_VIDEO_URL";
	
	public static final String APP_NAME = "SupportMe";
	
	public static final String DEFAULT_ACCOUNT_NAME = "team.5.hinf@gmail.com";
	
    public final static String KEY_ACCOUNT_NAME = "KEY_ACCOUNT_NAME";
    
    public static String DIRECTORY_PATH = "";

    public static void initGlobals(Context context) {
        // By default the logging will go to the apps internal storage, not the external directory
        Globals.IS_DEBUG = false;
        Globals.IS_LOG_EXTERNAL = false;
        Globals.APP_DIRECTORY = "supportdemo";
        
        Globals.WOCKETS_SP_URI = Uri.parse("content://com.team5hinf.supportdemo.provider");
        Globals.PACKAGE_NAME = "com.team5hinf.supportdemo";

        Globals.initDataDirectories(context);
        DIRECTORY_PATH = Globals.EXTERNAL_DIRECTORY_PATH;

        Globals.SFTP_SERVER_USER_NAME = "sftpdownload";
        Globals.SFTP_SERVER_PASSWORD  = "$parRow1ark";
        Globals.SFTP_SERVER_URL       = "wockets.ccs.neu.edu";
    }

}
