var Template = {};
Template["noPageFound"] = [
    {
        "tag": "center.h1",
        "text": {
            "tag": "span",
            "className": "",
            "name": "date",
            "text": "Page not found"
        }
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["noDataFound"] = [
    {
        "tag": "center.h1",
        "text": "No data found"
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["loading"] = [
    {
        "tag": "div.center",
        "className": "loading",
        "text": "Loading..."
    }
];
Template["heading"] = [
    {
        "tag": "div.center.table.tbody.tr",
        "className": "heading",
        "text": [
            {
                "tag": "td",
                "className": "text-center pl-5px",
                "text": [
                    {
                        "tag": "div",
                        "text": "Heading Line 1"
                    },
                    {
                        "tag": "div",
                        "text": "Larger Heading Line 2 Row"
                    }
                ]
            }
        ]
    }
];
Template["link"] = [
    {
        "tag": "div.center",
        "text": [
            {
                "tag": "a",
                "href": "/change_password",
                "name": "link.change_password",
                "className": "p-5px",
                "text": "Change Password"
            },
            {
                "tag": "a",
                "name": "link.logout",
                "href": "/logout",
                "className": "p-5px",
                "text": "Logout"
            }
        ]
    },
    {
        "tag": "div.center",
        "text": [
            {
                "tag": "span",
                "text": "Login as"
            },
            {
                "tag": "span",
                "className": "small",
                "name": "isAdminTextDisplayEnable",
                "text": " (Admin)"
            },
            {
                "tag": "span",
                "className": "",
                "text": ": "
            },
            {
                "tag": "b",
                "name": "link.loginAs",
                "text": ""
            }
        ]
    }
];
Template["login"] = [
    {
        "tag": "div.div",
        "className": "form-div pt-25px",
        "text": {
            "tag": "form",
            "id": "login_form",
            "name": "login_form",
            "value": "login_form",
            "text": [
                {
                    "tag": "div.h1",
                    "className": "p-10px",
                    "text": "Login"
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Username"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "login.username",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Password"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "login.password",
                                    "type": "password",
                                    "text": ""
                                }
                            ]
                        }
                    ]
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "button",
                                    "className": "btn btn-primary",
                                    "name": "login.submit",
                                    "text": "Submit"
                                },
                                {
                                    "tag": "a",
                                    "href": "/forgot_password",
                                    "className": "p-5px",
                                    "text": "Forgot Password"
                                },
                                {
                                    "tag": "a",
                                    "href": "/register",
                                    "className": "p-5px",
                                    "text": "Register"
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "className": "text-center d-none",
                            "name": "login.guest-login-link",
                            "text": [
                                {
                                    "tag": "button",
                                    "className": "btn btn-link",
                                    "name": "login.submit-guest",
                                    "text": "Don't have account?, Continue as Guest."
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["logout"] = [
    {
        "tag": "div.center",
        "text": ""
    }
];
Template["forgot_password"] = [
    {
        "tag": "div.div",
        "className": "form-div pt-25px",
        "text": {
            "tag": "form",
            "id": "forgot_password",
            "name": "forgot_password_form",
            "value": "forgot_password_form",
            "text": [
                {
                    "tag": "div.h1",
                    "className": "p-10px",
                    "text": "Forgot Password"
                },
                {
                    "tag": "div",
                    "name": "forgot_password.fields",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Username"
                                },
                                {
                                    "tag": "input",
                                    "type": "text",
                                    "className": "form-control",
                                    "name": "forgot_password.username"
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Registered Mobile"
                                },
                                {
                                    "tag": "input",
                                    "type": "text",
                                    "className": "form-control",
                                    "name": "forgot_password.mobile"
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Registered Email"
                                },
                                {
                                    "tag": "input",
                                    "type": "text",
                                    "className": "form-control",
                                    "name": "forgot_password.email"
                                }
                            ]
                        }
                    ]
                },
                {
                    "tag": "div",
                    "name": "forgot_password.links",
                    "className": "form-group",
                    "text": {
                        "tag": "div",
                        "text": [
                            {
                                "tag": "button",
                                "className": "btn btn-primary",
                                "name": "forgot_password.submit",
                                "text": "Submit"
                            },
                            {
                                "tag": "a",
                                "href": "/login",
                                "className": "p-5px",
                                "text": "Login"
                            },
                            {
                                "tag": "a",
                                "href": "/register",
                                "className": "p-5px",
                                "text": "Register"
                            },
                            {
                                "tag": "a",
                                "href": "/create_password",
                                "className": "p-5px d-none",
                                "name": "displayCreatePasswordLinkEnable",
                                "text": "Create Password"
                            }
                        ]
                    }
                }
            ]
        }
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["register"] = [
    {
        "tag": "div.div",
        "className": "form-div pt-25px",
        "text": {
            "tag": "form",
            "id": "register_form",
            "name": "register_form",
            "value": "register_form",
            "text": [
                {
                    "tag": "div.h1",
                    "className": "p-10px",
                    "text": "Register"
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Username"
                                },
                                {
                                    "tag": "input",
                                    "type": "text",
                                    "className": "form-control",
                                    "name": "register.username"
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": [
                                        {
                                            "tag": "span",
                                            "text": "Passcode"
                                        },
                                        {
                                            "tag": "span",
                                            "className": "small pl-5px",
                                            "text": "(Receive from admin)"
                                        }
                                    ]
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "register.passcode",
                                    "type": "text",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": [
                                        {
                                            "tag": "span",
                                            "text": "New Password"
                                        }
                                    ]
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "register.password",
                                    "type": "password",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Name"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "register.displayName",
                                    "type": "text",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Mobile"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "register.mobile",
                                    "type": "text",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Email"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "register.email",
                                    "type": "text",
                                    "text": ""
                                }
                            ]
                        }
                    ]
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "button",
                                    "className": "btn btn-primary",
                                    "name": "register.submit",
                                    "text": "Submit"
                                },
                                {
                                    "tag": "a",
                                    "href": "/login",
                                    "className": "p-5px",
                                    "text": "Login"
                                },
                                {
                                    "tag": "a",
                                    "href": "/forgot_password",
                                    "className": "p-5px",
                                    "text": "Forgot Password"
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["change_password"] = [
    {
        "tag": "div.div",
        "className": "form-div",
        "text": {
            "tag": "form",
            "id": "change_password",
            "name": "change_password_form",
            "value": "change_password_form",
            "text": [
                {
                    "tag": "div.h1",
                    "className": "p-10px",
                    "text": "Change Password"
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Old Password"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "change_password.old_password",
                                    "type": "password",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": [
                                        {
                                            "tag": "span",
                                            "text": "New Password"
                                        }
                                    ]
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "change_password.new_password",
                                    "type": "password",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Confirm Password"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "change_password.confirm_password",
                                    "type": "password",
                                    "text": ""
                                }
                            ]
                        }
                    ]
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": {
                        "tag": "button",
                        "className": "btn btn-primary",
                        "name": "change_password.submit",
                        "text": "Submit"
                    }
                }
            ]
        }
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["create_password"] = [
    {
        "tag": "div.div",
        "className": "form-div pt-25px",
        "text": {
            "tag": "form",
            "id": "create_password",
            "name": "create_password_form",
            "value": "create_password_form",
            "text": [
                {
                    "tag": "div.h1",
                    "className": "p-10px",
                    "text": "Create Password"
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Username"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "create_password.username",
                                    "type": "text",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": [
                                        {
                                            "tag": "span",
                                            "text": "Create password otp"
                                        },
                                        {
                                            "tag": "span",
                                            "className": "small pl-5px",
                                            "name": "create_password.otp-instruction",
                                            "text": "(Receive from admin)"
                                        }
                                    ]
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "create_password.create_password_otp",
                                    "type": "text",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": [
                                        {
                                            "tag": "span",
                                            "text": "New Password"
                                        }
                                    ]
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "create_password.new_password",
                                    "type": "password",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Confirm Password"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "create_password.confirm_password",
                                    "type": "password",
                                    "text": ""
                                }
                            ]
                        }
                    ]
                },
                {
                    "tag": "div",
                    "text": [
                        {
                            "tag": "button",
                            "className": "btn btn-primary",
                            "name": "create_password.submit",
                            "text": "Submit"
                        },
                        {
                            "tag": "a",
                            "href": "/login",
                            "className": "p-5px",
                            "text": "Login"
                        },
                        {
                            "tag": "a",
                            "href": "/forgot_password",
                            "className": "p-5px",
                            "text": "Forgot Password"
                        },
                        {
                            "tag": "a",
                            "href": "/register",
                            "className": "p-5px",
                            "text": "Register"
                        }
                    ]
                }
            ]
        }
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["footerLinkJson"] = {};
Template["footerLinkJsonAfterLogin"] = {};

export default Template;
