<!DOCTYPE html>
<html>
<head>
    <title>Assign Supplier</title>
</head>
<body>
    <h1>Assign Supplier to Order #${order.id}</h1>
    <p><strong>Resource:</strong> ${order.resource.name} (${order.quantity} ${order.resource.unit})</p>
    <p><strong>Delivery Date:</strong> ${order.deliveryDate}</p>

    <form action="/orders/${order.id}/assign" method="post">
        <div>
            <label>Supplier:
                <select name="supplierId" required>
                    <#list suppliers as supplier>
                        <option value="${supplier.id}">${supplier.name} (${supplier.contactInfo})</option>
                    </#list>
                </select>
            </label>
        </div>
        <button type="submit">Assign Supplier</button>
    </form>

    <a href="/orders">Back to Orders</a>
</body>
</html>