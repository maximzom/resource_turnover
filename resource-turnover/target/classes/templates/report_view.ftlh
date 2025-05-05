<!DOCTYPE html>
<html>
<head>
    <title>Supply Report</title>
</head>
<body>
    <h1>Supply Report</h1>
    <table border="1">
        <tr>
            <th>Order ID</th>
            <th>Resource</th>
            <th>Quantity</th>
            <th>Created</th>
            <th>Status</th>
            <th>Supplier</th>
        </tr>
        <#list orders as order>
        <tr>
            <td>${order.id}</td>
            <td>${order.resource.name}</td>
            <td>${order.quantity}</td>
            <td>${order.creationDate}</td>
            <td>${order.status}</td>
            <td>${order.supplier.name!'-'}</td>
        </tr>
        </#list>
    </table>
    <a href="/reports">New Report</a>
    <a href="/">Home</a>
</body>
</html>