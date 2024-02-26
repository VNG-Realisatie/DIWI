import * as Paths from "../Paths";

export async function diwiFetch(input: RequestInfo | URL, init?: RequestInit | undefined) {
    const options = { ...init };

    options.headers = {
        ...options.headers,
        "X-Requested-With": "XMLHttpRequest",
    };

    return fetch(input, options).then((response) => {
        if (response.status === 401) {
            window.location.href = Paths.login.path;
        }
        return response;
    });
}

export async function download(url: string, filename: string) {
    const data = await diwiFetch(url);
    const blob = await data.blob();
    const downloadedDataURL = URL.createObjectURL(blob);

    const anchor = document.createElement("a");
    anchor.href = downloadedDataURL;
    anchor.download = filename;

    document.body.appendChild(anchor);
    anchor.click();
    document.body.removeChild(anchor);

    URL.revokeObjectURL(downloadedDataURL);
}

export async function getJson(url: string) {
    const res = await diwiFetch(encodeURI(url));

    if (!res.ok) {
        throw Error(res.statusText);
    }

    return res.json();
}

export async function postJson(url: string, data: any) {
    const body = JSON.stringify(data);

    const res = await diwiFetch(encodeURI(url), {
        method: "POST",
        body: body,
    });

    if (!res.ok) {
        throw Error(res.statusText);
    }

    return res.json();
}

export async function putJson(url: string, data: any) {
    const body = JSON.stringify(data);

    const res = await diwiFetch(encodeURI(url), {
        method: "PUT",
        body: body,
    });

    if (!res.ok) {
        throw Error(res.statusText);
    }

    return res.json();
}

export async function deleteJson(url: string) {
    const res = await diwiFetch(encodeURI(url), {
        method: "DELETE",
    });

    if (!res.ok) {
        throw Error(res.statusText);
    }

    return res.json();
}
