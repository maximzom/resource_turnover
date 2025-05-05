<!DOCTYPE html>
<html>
<head>
    <title>Order #${order.id} Comments</title>
</head>
<body>
    <h1>Comments for Order #${order.id}</h1>
    <p><strong>Resource:</strong> ${order.resource.name} (${order.quantity} ${order.resource.unit})</p>
    <p><strong>Status:</strong> ${order.status}</p>

    <h2>Existing Comments</h2>
    <#if order.comments?? && order.comments?size gt 0>
        <#list order.comments as comment>
            <div>
                <p><strong>${comment.author}</strong> on ${comment.date}</p>
                <p>${comment.content}</p>
                <hr>
            </div>
        </#list>
    <#else>
        <p>No comments yet.</p>
    </#if>

    <h2>Add Comment</h2>
    <form action="/orders/${order.id}/comments" method="post">
        <div>
            <label>Your Name: <input type="text" name="author" required></label>
        </div>
        <div>
            <label>Comment: <textarea name="content" required></textarea></label>
        </div>
        <button type="submit">Add Comment</button>
    </form>

    <a href="/orders">Back to Orders</a>
</body>
</html>