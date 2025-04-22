<!DOCTYPE html>
<html>
<head>
    <title><#if resource.id??>Edit<#else>Add</#if> Resource</title>
</head>
<body>
    <h1><#if resource.id??>Edit<#else>Add New</#if> Resource</h1>
    <form action="/resources<#if resource.id??>/${resource.id}</#if>" method="post">
        <div>
            <label>Name: <input type="text" name="name" value="${resource.name!''}" required></label>
        </div>
        <div>
            <label>Type: <input type="text" name="type" value="${resource.type!''}" required></label>
        </div>
        <div>
            <label>Quantity: <input type="number" name="quantity" value="${resource.quantity!0}" step="0.01" min="0" required></label>
        </div>
        <div>
            <label>Unit: <input type="text" name="unit" value="${resource.unit!''}" required></label>
        </div>
        <div>
            <label>Price: <input type="number" name="price" value="${resource.price!0}" step="0.01" min="0" required></label>
        </div>
        <button type="submit">Save</button>
    </form>
    <a href="/resources">Cancel</a>
</body>
</html>