<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Page de connexion</title>
<link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
</head>

<body>
	<h1>Login :</h1>
	<s:form name="myForm" action="controller.Connect.login.action"
		method="POST">
		<s:textfield label="Code user" name="userCde" />
		<s:password label="Password" name="userPwd" />
		<s:submit name="submit" />
	</s:form>
		<s:form name="myFormRetour" action="retourAccueil" method="POST">
			<s:submit name="Retour" value="Retour à l'accueil" />
		</s:form>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>