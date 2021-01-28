package com.sharedpro.JwtToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import io.jsonwebtoken.*;
import java.util.*;
import javax.servlet.http.*;
@Controller
public class RegistrationFormHandler
{
@RequestMapping(value="/",method=RequestMethod.GET)
public String RegistrationForm()
{
return "RegistrationForm";
}

@RequestMapping(value="/handler",method=RequestMethod.POST)
public String Handler(@RequestParam String firstname, @RequestParam String lastname,@RequestParam String mobileNumber, @RequestParam String email,@RequestParam String username,@RequestParam String password, HttpServletResponse response)
{
Map<String,Object> map=new HashMap<>();
map.put("firstname",firstname);
map.put("lastname",lastname);
map.put("mobileNumber",mobileNumber);
map.put("email",email);
map.put("username",username);
map.put("password",password);
SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("SharedPro");
Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
JwtBuilder builder = Jwts.builder().setClaims(map).signWith(signatureAlgorithm, signingKey);
System.out.println(builder.compact());
Cookie tokenCookie=new Cookie("Authorization",builder.compact());
response.addCookie(tokenCookie);
return "redirect:/showData";
}

@RequestMapping(value="/showData", method=RequestMethod.GET)
public String showData(HttpSession session, HttpServletRequest request)
{
String jwtToken=null;
Cookie[] cookies=request.getCookies();
if(cookies!=null)
{
for(Cookie cookie:cookies)
{
if(cookie.getName().equals("Authorization"))
{
jwtToken=cookie.getValue();
break;
}
}
}
Claims claims = Jwts.parser()         
       .setSigningKey(DatatypeConverter.parseBase64Binary("SharedPro"))
       .parseClaimsJws(jwtToken).getBody();
session.setAttribute("firstname",claims.get("firstname"));
session.setAttribute("lastname",claims.get("lastname"));
session.setAttribute("mobileNumber",claims.get("mobileNumber"));
session.setAttribute("email",claims.get("email"));
session.setAttribute("username",claims.get("username"));
session.setAttribute("password",claims.get("password"));
return "Data";
}

}