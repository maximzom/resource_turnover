<!DOCTYPE html>
<html>
<head>
    <title>Generate Report</title>
</head>
<body>
    <h1>Generate Supply Report</h1>
    <form action="/reports" method="post">
        <div>
            <label>Start Date: <input type="date" name="startDate"></label>
        </div>
        <div>
            <label>End Date: <input type="date" name="endDate"></label>
        </div>
        <div>
            <label>Status:
                <select name="status">
                    <option value="">All Statuses</option>
                    <#list allStatuses as status>
                        <option value="${status}">${status}</option>
                    </#list>
                </select>
            </label>
        </div>
        <button type="submit">Generate Report</button>
    </form>
    <a href="/">Back to Home</a>
</body>
</html>