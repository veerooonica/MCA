function defaultErrorHandler(jqXHR, exception) {
    let msg = jqXHR.responseJSON ? jqXHR.responseJSON.message : null;
    console.log(jqXHR)
    console.error(exception)
    if (msg) {
        showMessage(msg, 3000, () => {
            if (jqXHR.status === 401 || jqXHR.status === 403) {
                window.location.href = '/login'
            }
        });
        return;
    }
    if (jqXHR.status === 0) {
        msg = 'Not connect.\n Verify Network.';
    } else if (jqXHR.status == 404) {
        msg = 'Requested page not found. [404]';
    } else if (jqXHR.status == 500) {
        msg = 'Internal Server Error [500].';
    } else if (exception === 'parsererror') {
        msg = 'Requested JSON parse failed.';
    } else if (exception === 'timeout') {
        msg = 'Time out error.';
    } else if (exception === 'abort') {
        msg = 'Ajax request aborted.';
    } else {
        msg = 'Uncaught Error.\n' + jqXHR.responseText;
    }
    showMessage(msg);
}

const ajaxPOST = (url, body, onSuccess = () => {
}) => {
    $.ajax({
        type: "POST",
        headers: getHeaders(),
        url: url,
        data: JSON.stringify(body),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        encode: true,
        success: onSuccess,
        error: defaultErrorHandler,
    })
}

const ajaxPOSTWithoutResponse = (url, body, onSuccess = () => {
}) => {
    $.ajax({
        type: "POST",
        headers: getHeaders(),
        url: url,
        data: JSON.stringify(body),
        contentType: "application/json; charset=utf-8",
        encode: true,
        success: onSuccess,
        error: defaultErrorHandler,
    })
}

const ajaxGET = (url, onSuccess) => {
    $.ajax({
        type: "GET",
        headers: getHeaders(),
        url: url,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        encode: true,
        success: onSuccess,
        error: defaultErrorHandler,
    })
}

const ajaxPUT = (url, body, onSuccess) => {
    $.ajax({
        type: "PUT",
        headers: getHeaders(),
        url: url,
        data: JSON.stringify(body),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        encode: true,
        success: onSuccess,
        error: defaultErrorHandler,
    })
}

const ajaxPUTWithoutResponse = (url, body, onSuccess = () => {
}) => {
    $.ajax({
        type: "PUT",
        headers: getHeaders(),
        url: url,
        data: JSON.stringify(body),
        contentType: "application/json; charset=utf-8",
        encode: true,
        success: onSuccess,
        error: defaultErrorHandler,
    })
}

const ajaxDELETE = (url, onSuccess) => {
    $.ajax({
        type: "DELETE",
        headers: getHeaders(),
        url: url,
        success: onSuccess,
        error: defaultErrorHandler,
    })
}

const getHeaders = () => {
    if (localStorage.getItem("X-Token")) {
        return {
            "Authorization": "Bearer " + localStorage.getItem("X-Token")
        }
    }
    return {};
}