package io.ebinar.infolder.api;


import android.util.Log;

import com.blueprint.blueprint.database.BPDatabaseManager;
import com.blueprint.blueprint.http.BPApi;
import com.blueprint.blueprint.http.BPHttpType;
import com.blueprint.blueprint.object.BPObject;

import java.util.Date;

/**
 * Creado Por jorgeacostaalvarado el 14-10-15.
 */
public class MTApi extends BPApi {

    private String urlbase = "http://200.75.31.3/infoldermobileservices/infoldermobile.svc";

    public MTApi()
    {
        addHeader("Content-Type", "application/json");
    }

    public void login(String email, String password, BPApiCallback callback){

        String urlLogin = urlbase + "/login";

        setCallback(callback);

        BPObject object = new BPObject();
        object.setString("username", email);
        object.setString("email", email);
        object.setString("password", password);

        setEntity(object.toString());

        requestFromUrl(urlLogin, BPHttpType.POST);

    }

    public void setFav(String mediaId, boolean value, BPApiCallback callback){
        String urlLogin = urlbase + "/media/" + mediaId;

        setCallback(callback);

        BPObject object = new BPObject();
        object.setBoolean("is_favorite", value);

        setEntity(object.toString());

        BPObject current = MTUser.getCurrenUser(BPDatabaseManager.getContext());

        if(current != null) {
            addHeader("X-Infolder-Session-Token", current.getString("sessionToken"));
        }


        requestFromUrl(urlLogin, BPHttpType.PUT);
    }

    public void signup(BPObject data, BPApiCallback callback){
        String urlLogin = urlbase + "/users";
        setCallback(callback);
        setEntity(data.toString());
        requestFromUrl(urlLogin, BPHttpType.POST);
    }

    public void requestPassword(String email, BPApiCallback callback){
        String urlLogin = urlbase + "/requestPasswordReset";
        setCallback(callback);

        BPObject object = new BPObject();
        object.setString("email", email);

        setEntity(object.toString());
        requestFromUrl(urlLogin, BPHttpType.POST);
    }

    public void syncUser(BPObject object, BPApiCallback callback){
        String urlLogin = urlbase + "/users/me";
        setCallback(callback);

        BPObject user = MTUser.getCurrenUser(BPDatabaseManager.getContext());
        if(object!=null) {
            addHeader("X-Infolder-Session-Token", user.getString("sessionToken"));
        }

        setEntity(object.toString());

        requestFromUrl(urlLogin, BPHttpType.POST);
    }

    public void getBrands(boolean owner, BPApiCallback callback){

        String urlBrand = urlbase + "/brands?filter=24hours";

        if(owner){
            urlBrand +="&owner=true";
        }

        setCallback(callback);


        BPObject user = MTUser.getCurrenUser(BPDatabaseManager.getContext());


        if(user!=null) {
            addHeader("X-Infolder-Session-Token", user.getString("sessionToken"));

            BPObject object = new BPObject();
            setEntity(object.toString());
        }


        requestFromUrl(urlBrand, BPHttpType.GET);
    }

    public void getHistorial(String objectId, BPApiCallback callback){



        String urlBrand = urlbase + "/media/" + objectId;
        setCallback(callback);


        BPObject user = MTUser.getCurrenUser(BPDatabaseManager.getContext());


        if(user!=null) {
            addHeader("X-Infolder-Session-Token", user.getString("sessionToken"));
            BPObject object = new BPObject();
            setEntity(object.toString());
        }


        requestFromUrl(urlBrand, BPHttpType.GET);
    }

    public void getMedia(String brand, String filter,int media, BPApiCallback callback){
        String urlBrand = urlbase + "/media?filter=" + filter + "&medio=" + String.valueOf(media) + "&brand=" + brand;

        setCallback(callback);
        BPObject object = new BPObject();
        setEntity(object.toString());

        BPObject current = MTUser.getCurrenUser(BPDatabaseManager.getContext());

        if(current != null) {
            addHeader("X-Infolder-Session-Token", current.getString("sessionToken"));
        }

        requestFromUrl(urlBrand, BPHttpType.GET);
    }

    public void getSearch(String search, String since, String to,int media, BPApiCallback callback){
        String urlBrand = urlbase + "/search?filter=" + search + "&medio=" + String.valueOf(media) + "&since=" + since+"&to="+to;



        setCallback(callback);
        BPObject object = new BPObject();
        setEntity(object.toString());

        BPObject current = MTUser.getCurrenUser(BPDatabaseManager.getContext());

        if(current != null) {
            addHeader("X-Infolder-Session-Token", current.getString("sessionToken"));
        }

        requestFromUrl(urlBrand, BPHttpType.GET);
    }

    public void getFavorites(BPApiCallback callback){
        String urlBrand = urlbase + "/favorites";



        setCallback(callback);
        BPObject object = new BPObject();
        setEntity(object.toString());

        BPObject current = MTUser.getCurrenUser(BPDatabaseManager.getContext());

        if(current != null) {
            addHeader("X-Infolder-Session-Token", current.getString("sessionToken"));
        }

        requestFromUrl(urlBrand, BPHttpType.GET);
    }

    public void refreshMedia(String mediaId, BPApiCallback callback){
        String urlBrand = urlbase + "/media/" + mediaId;

        setCallback(callback);
        BPObject object = new BPObject();
        setEntity(object.toString());

        BPObject current = MTUser.getCurrenUser(BPDatabaseManager.getContext());

        if(current != null) {
            addHeader("X-Infolder-Session-Token", current.getString("sessionToken"));
        }

        requestFromUrl(urlBrand, BPHttpType.GET);
    }

    public void getMe(BPApiCallback callback){
        String urlBrand = urlbase + "/users/me" ;


        setCallback(callback);
        BPObject object = new BPObject();
        setEntity(object.toString());

        BPObject current = MTUser.getCurrenUser(BPDatabaseManager.getContext());

        if(current != null) {
            addHeader("X-Infolder-Session-Token", current.getString("sessionToken"));
        }

        requestFromUrl(urlBrand, BPHttpType.GET);
    }


}
