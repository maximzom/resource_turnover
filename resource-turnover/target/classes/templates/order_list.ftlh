<!DOCTYPE html>
<html>
<head>
    <title>Orders</title>
</head>
<body>
    <h1>Orders</h1>
    <a href="/orders/new">New Order</a>
    <table border="1">
        <tr><th>ID</th><th>Resource</th><th>Qty</th><th>Delivery</th><th>Status</th><th>Actions</th></tr>
        <#list orders as o>
        <tr>
            <td>${o.id}</td>
            <td>${o.resource.name}</td>
            <td>${o.quantity}</td>
            <td>${o.deliveryDate}</td>
            <td>${o.status}</td>
            <td>
                <#if o.status == 'PENDING_SUPPLY'><a href="/orders/${o.id}/assign">Assign</a></#if>
                <a href="/orders/${o.id}/comments">Comments</a>
            </td>
        </tr>
        </#list>
    </table>
    <a href="/">Back</a>
</body>
</html>