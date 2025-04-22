<!DOCTYPE html>
<html>
<head>
    <title>Agricultural Resources</title>
</head>
<body>
    <h1>Agricultural Resources</h1>
    <a href="/resources/new">Add New Resource</a>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Quantity</th>
            <th>Unit</th>
            <th>Price</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        <#list resources as resource>
        <tr>
            <td>${resource.id}</td>
            <td>${resource.name}</td>
            <td>${resource.type}</td>
            <td>${resource.quantity}</td>
            <td>${resource.unit}</td>
            <td>${resource.price}</td>
            <td>${resource.active?then('Active','Inactive')}</td>
            <td>
                <a href="/resources/${resource.id}/edit">Edit</a>
                <#if resource.active>| <a href="/resources/${resource.id}/archive">Archive</a></#if>
            </td>
        </tr>
        </#list>
    </table>
    <a href="/">Back</a>
</body>
</html>