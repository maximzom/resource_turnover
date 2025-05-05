<!DOCTYPE html>
<html>
<head>
    <title><#if supplier.id??>Edit<#else>Add</#if> Supplier</title>
</head>
<body>
    <h1><#if supplier.id??>Edit<#else>Add</#if> Supplier</h1>
    <form action="/suppliers<#if supplier.id??>/${supplier.id}</#if>" method="post">
        <div>
            <label>Name: <input type="text" name="name" value="${supplier.name!''}" required></label>
        </div>
        <div>
            <label>Contact: <input type="text" name="contactInfo" value="${supplier.contactInfo!''}" required></label>
        </div>
        <button type="submit">Save</button>
    </form>
    <a href="/suppliers">Cancel</a>
</body>
</html>