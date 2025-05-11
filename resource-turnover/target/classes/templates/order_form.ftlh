<!DOCTYPE html>
<html>
<head>
    <title>New Order</title>
</head>
<body>
    <h1>New Order</h1>
    <form action="/orders" method="post">
        <div>
            <label>Resource:
                <select name="resourceId" required>
                    <#list resources as r>
                    <option value="${r.id}">${r.name} (${r.quantity} ${r.unit})</option>
                    </#list>
                </select>
            </label>
        </div>
        <div><label>Qty: <input type="number" name="quantity" step="0.01" min="0.01" required></label></div>
        <div><label>Delivery: <input type="date" name="deliveryDate" required></label></div>
        <button type="submit">Create</button>
    </form>
    <a href="/orders">Cancel</a>
</body>
</html>