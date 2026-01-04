<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Page de test</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        .container {
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 20px;
            margin-top: 20px;
        }
        h1 {
            color: #333;
        }
        .data-item {
            margin: 10px 0;
            padding: 10px;
            background-color: #fff;
            border: 1px solid #eee;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>${message}</h1>
        
        <div class="data-item">
            <h2>Données de l'utilisateur :</h2>
            <p><strong>Nom :</strong> ${utilisateur}</p>
            
            <h3>Données supplémentaires :</h3>
            <p><strong>Nom :</strong> ${nom}</p>
            <p><strong>Âge :</strong> ${age}</p>
            <p><strong>Ville :</strong> ${ville}</p>
        </div>
        
        <div class="data-item">
            <h3>Informations sur la requête :</h3>
            <p><strong>URL :</strong> ${pageContext.request.requestURL}</p>
            <p><strong>Méthode :</strong> ${pageContext.request.method}</p>
        </div>
    </div>
</body>
</html>
