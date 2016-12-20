package com.blueprint.blueprint.object;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creado Por jorgeacostaalvarado el 14-10-15.
 */
public class BPValidate {

   public static boolean validateEmail(String email){

       Pattern pattern =  Pattern.compile("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,3})$");
       Matcher tagmatch = pattern.matcher(email);

       return  tagmatch.matches();
   }
}
