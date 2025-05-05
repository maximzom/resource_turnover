<!DOCTYPE html>
<html>
<head>
    <title>Suppliers</title>
</head>
<body>
    <h1>Suppliers List</h1>
    <a href="/suppliers/new">Add New Supplier</a>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Contact</th>
            <th>Actions</th>
        </tr>
        <#list suppliers as supplier>
        <tr>
            <td>${supplier.id}</td>
            <td>${supplier.name}</td>
            <td>${supplier.contactInfo}</td>
            <td>
                <a href="/suppliers/${supplier.id}/edit">Edit</a>
                <form action="/suppliers/${supplier.id}/delete" method="post" style="display:inline">
                    <button type="submit">Delete</button>
                </form>
            </td>
        </tr>
        </#list>
    </table>
    <a href="/">Back to Home</a>
</body>
</html>