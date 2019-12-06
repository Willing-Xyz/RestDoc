var LOCAL_AUTH_KEY = "SWAGGER_AUTH_STORE";

function getAuthStore() {
    return JSON.parse(localStorage.getItem(LOCAL_AUTH_KEY))
}

function setAuthStore(value) {
    localStorage.setItem(LOCAL_AUTH_KEY, JSON.stringify(value))
}

function setAuthData(auth) {
    const store = getAuthStore() || {};
    Object.keys(auth).forEach(key => {
        store[key] = auth[key]
    });

    setAuthStore(store)
}

function removeAuthData(keys) {
    const store = getAuthStore() || {};
    keys.forEach(key => {
        delete store[key]
    });
    setAuthStore(store);
}

function handleAuthorizeAction() {
    var instance = window.ui.getSystem();
    var authSelectors = instance.authSelectors;
    var authActions = instance.authActions;
    var _authorize = authActions.authorize;
    var _logout = authActions.logout;
    // 覆盖原方法
    authActions.authorize = function (data) {
        _authorize(data);
        setAuthData(data);
    };
    authActions.logout = function (data) {
        _logout(data);
        removeAuthData(data)
    };

    var definitions = authSelectors.definitionsToAuthorize();
    if (definitions && definitions.size) {
        const state = {};
        const store = getAuthStore() || {};
        definitions.forEach(([array]) => {
            const [k] = array;
            if (store[k]) {
                state[k] = store[k]
            }
        });
        if (Object.keys(state)) {
            authActions.authorize(state)
        }
    }
}