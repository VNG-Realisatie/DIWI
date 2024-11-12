import * as Paths from "../Paths";

export async function diwiFetch(input: RequestInfo | URL, init?: RequestInit | undefined) {
    const options = { ...init };

    options.headers = {
        ...options.headers,
        "X-Requested-With": "XMLHttpRequest",
    };

    return fetch(input, options).then((response) => {
        if (response.status === 401) {
            const returnUrl = window.location.origin + window.location.pathname + window.location.search;
            // We can't use navigate here, because navigate will use the internal router and just show a 404
            window.location.href = `${Paths.login.path}?returnUrl=${encodeURIComponent(returnUrl)}`;
        }
        if (response.status === 403) {
            // not sure what we want to do here, probably nothing?
            console.error("Blocked by server while trying to diwiFetch:", input);
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
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function downloadPost(url: string, filename: string, body: any) {
    const stringifiedBody = JSON.stringify(body);
    const data = await diwiFetch(encodeURI(url), {
        method: "POST",
        body: stringifiedBody,
        headers: {
            "Content-Type": "application/json",
        },
    });
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

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function postJsonNoResponse(url: string, data: any) {
    const body = JSON.stringify(data);

    const res = await diwiFetch(encodeURI(url), {
        method: "POST",
        body: body,
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!res.ok) {
        if (res.status === 400) {
            const response = await res.json();
            throw Error(response.error);
        } else {
            throw Error(res.statusText);
        }
    }
    return res;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function postJson(url: string, data: any) {
    const res = await postJsonNoResponse(url, data);

    return res.json();
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export async function putJson(url: string, data: any) {
    const body = JSON.stringify(data);

    const res = await diwiFetch(encodeURI(url), {
        method: "PUT",
        body: body,
        headers: {
            "Content-Type": "application/json",
        },
    });

    if (!res.ok) {
        if (res.status === 400) {
            const response = await res.json();
            throw Error(response.error);
        } else {
            throw Error(res.statusText);
        }
    }

    return res.json();
}

export async function deleteJson(url: string) {
    const res = await diwiFetch(encodeURI(url), {
        method: "DELETE",
    });
    if (res.status === 204) {
        return res;
    }
    if (!res.ok) {
        throw Error(res.statusText);
    }

    return res.json();
}
