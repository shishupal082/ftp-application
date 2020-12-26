var Template = {};
Template["heading"] = [
    {
        "tag": "div.center.table.tbody.tr",
        "className": "heading",
        "text": [
            {
                "tag": "td",
                "text": {
                    "tag": "img",
                    "className": "logo",
                    "src": "/assets/static/img/railwayslogo-128.png"
                }
            },
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
                "href": "/dashboard",
                "name": "link.dashboard",
                "className": "p-5px",
                "text": "Dashboard"
            },
            {
                "tag": "a",
                "href": "/change_password",
                "name": "link.change_password",
                "className": "p-5px",
                "text": "Change Password"
            },
            {
                "tag": "a",
                "href": "/upload_file",
                "name": "link.upload_file",
                "className": "p-5px",
                "text": "Upload File"
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
                "name": "link.is-admin",
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
                                    "name": "login.create_password-link",
                                    "href": "/create_password",
                                    "className": "p-5px d-none",
                                    "text": "Create Password"
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
                            "className": "text-center",
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
Template["forgot_password"] = [
    {
        "tag": "div.div",
        "className": "form-div pt-25px",
        "text": {
            "tag": "form",
            "id": "forgot_password",
            "text": [
                {
                    "tag": "div.h1",
                    "className": "p-10px",
                    "text": "Forgot Password"
                },
                {
                    "tag": "div",
                    "name": "forgot_password.old-text",
                    "className": "form-group pl-10px d-none",
                    "text": [
                        {
                            "tag": "div",
                            "className": "",
                            "text": [
                                {
                                    "tag": "label",
                                    "name": "forgot_password.page-instruction",
                                    "text": "Please contact admin."
                                }
                            ]
                        }
                    ]
                },
                {
                    "tag": "div",
                    "name": "forgot_password.old-link",
                    "className": "form-group d-none",
                    "text": [
                        {
                            "tag": "div",
                            "className": "pl-5px",
                            "text": {
                                "tag": "div",
                                "text": [
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
                                        "name": "forgot_password_disable.create_password-link",
                                        "text": "Create Password"
                                    }
                                ]
                            }
                        }
                    ]
                },
                {
                    "tag": "div",
                    "name": "forgot_password.fields",
                    "className": "form-group d-none",
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
                    "className": "form-group d-none",
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
                                "name": "forgot_password.create_password-link",
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
                                },
                                {
                                    "tag": "a",
                                    "name": "register.create_password-link",
                                    "href": "/create_password",
                                    "className": "p-5px d-none",
                                    "text": "Create Password"
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
Template["upload_file"] = [
    {
        "tag": "div.div",
        "className": "form-div",
        "text": {
            "tag": "form",
            "id": "upload_file_form",
            "text": [
                {
                    "tag": "div.h1",
                    "className": "p-10px",
                    "text": "Upload File"
                },
                {
                    "tag": "div",
                    "className": "form-group",
                    "text": [
                        {
                            "tag": "div",
                            "className": "d-none",
                            "name": "upload_file.subject.div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Subject"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "upload_file.subject",
                                    "type": "text",
                                    "text": ""
                                }
                            ]
                        },
                        {
                            "tag": "div",
                            "className": "d-none",
                            "name": "upload_file.heading.div",
                            "text": [
                                {
                                    "tag": "label",
                                    "text": "Heading"
                                },
                                {
                                    "tag": "input",
                                    "className": "form-control",
                                    "name": "upload_file.heading",
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
                                    "tag": "input",
                                    "type": "file",
                                    "name": "upload_file.file",
                                    "id": "file"
                                },
                                {
                                    "tag": "div.span",
                                    "name": "upload_file.message",
                                    "className": "small",
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
                            "tag": "button",
                            "className": "btn btn-primary",
                            "name": "upload_file.submit",
                            "text": "Submit"
                        },
                        {
                            "tag": "span",
                            "className": "pl-10px text-success",
                            "name": "upload_file.complete-status",
                            "text": ""
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
Template["dashboard"] = [
    {
        "tag": "div",
        "className": "dashboard",
        "text": [
            {
                "tag": "div",
                "className": "container",
                "text": {
                    "tag": "table.tbody.tr",
                    "className": "table",
                    "text": [
                        {
                            "tag": "td",
                            "text": {
                                "tag": "div",
                                "className": "dashboard-info-col",
                                "text": {
                                    "tag": "table.tbody",
                                    "className": "table table-striped",
                                    "name": "dashboardRow",
                                    "text": []
                                }
                            }
                        },
                        {
                            "tag": "td",
                            "className": "pdf-embed-td",
                            "text": [
                                {
                                    "tag": "div",
                                    "className": "pdf-view",
                                    "name": "dashboard.display.object.div",
                                    "text": {
                                        "tag": "object",
                                        "name": "pdfViewObject",
                                        "type": "application/pdf",
                                        "className": "pdf-embed",
                                        "data": "",
                                        "text": {
                                            "tag": "embed",
                                            "name": "pdfViewEmbed",
                                            "type": "application/pdf",
                                            "src": ""
                                        }
                                    }
                                },
                                {
                                    "tag": "div",
                                    "className": "img-view",
                                    "name": "dashboard.display.img.div",
                                    "text": {
                                        "tag": "img",
                                        "name": "dashboard.display.img",
                                        "src": "",
                                        "alt": "",
                                        "className": "display-img"
                                    }
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["dashboardOrderByOption"] = [
    {
        "tag": "tr",
        "text": [
            {
                "tag": "td",
                "colSpan": 2,
                "name": "dashboard.orderbydropdown.td",
                "text": {
                    "tag": "div",
                    "text": {
                        "tag": "select",
                        "name": "dashboard.orderbydropdown",
                        "className": "custom-select",
                        "value": "orderByDate",
                        "text": [
                            {
                                "tag": "option",
                                "value": "orderByDate",
                                "text": "Order By Date"
                            },
                            {
                                "tag": "option",
                                "value": "orderByUsername",
                                "text": "Order By Username"
                            }
                        ]
                    }
                }
            }
        ]
    }
];
Template["dashboardRowHeading"] = [
    {
        "tag": "tr",
        "text": [
            {
                "tag": "td",
                "colSpan": 2,
                "name": "dashboardRowHeading.heading",
                "className": "p-0",
                "text": {
                    "tag": "span",
                    "name": "rowHeading",
                    "className": "badge badge-primary",
                    "text": ""
                }
            }
        ]
    }
];
Template["dashboard1stRow"] = [
    {
        "tag": "tr",
        "text": [
            {
                "tag": "th",
                "className": "dashboard-s-no",
                "text": "S.No."
            },
            {
                "tag": "th",
                "className": "dashboard1stRow-fileinfo",
                "text": "Fileinfo"
            }
        ]
    }
];
Template["dashboard1stRowByDate"] = [
    {
        "tag": "tr",
        "text": [
            {
                "tag": "th",
                "className": "dashboard-s-no",
                "text": "S.No."
            },
            {
                "tag": "th",
                "className": "dashboard-orderbydate-username",
                "text": "Username"
            },
            {
                "tag": "th",
                "className": "dashboard1stRowByDate-fileinfo",
                "text": "Fileinfo"
            }
        ]
    }
];
Template["dashboardRowData"] = [
    {
        "tag": "tr",
        "text": [
            {
                "tag": "td",
                "name": "dashboardRowData.s.no",
                "className": "dashboard-s-no",
                "text": ""
            },
            {
                "tag": "td",
                "name": "dashboardRowData.fileinfo",
                "text": ""
            }
        ]
    }
];
Template["dashboardRowDataByDate"] = [
    {
        "tag": "tr",
        "text": [
            {
                "tag": "td",
                "name": "dashboardRowDataByDate.s.no",
                "className": "dashboard-s-no",
                "text": ""
            },
            {
                "tag": "td",
                "name": "dashboardRowDataByDate.username",
                "className": "dashboardRowDataByDate-username",
                "text": ""
            },
            {
                "tag": "td",
                "name": "dashboardRowDataByDate.fileinfo",
                "text": ""
            }
        ]
    }
];
Template["dashboard.fileinfo"] = [
    {
        "tag": "div",
        "name": "dashboard.fileinfo.filename",
        "text": "Filename"
    },
    {
        "tag": "div",
        "name": "dashboard.fileinfo.subject_heading",
        "text": {
            "tag": "table.tbody",
            "className": "table table-bordered mb-0",
            "text": [
                {
                    "tag": "tr",
                    "text": [
                        {
                            "tag": "td.b",
                            "className": "w-75px",
                            "text": "Subject"
                        },
                        {
                            "tag": "td",
                            "name": "dashboard.fileinfo.subject",
                            "text": ""
                        }
                    ]
                },
                {
                    "tag": "tr",
                    "text": [
                        {
                            "tag": "td.b",
                            "className": "w-75px",
                            "text": "Heading"
                        },
                        {
                            "tag": "td",
                            "name": "dashboard.fileinfo.heading",
                            "text": ""
                        }
                    ]
                }
            ]
        }
    },
    {
        "tag": "div",
        "text": [
            {
                "tag": "button",
                "name": "dashboard.fileinfo.view",
                "className": "btn btn-link pdf-view-link",
                "text": "View"
            },
            {
                "tag": "a",
                "href": "open file path",
                "name": "dashboard.fileinfo.open-in-new-tab",
                "isTargetBlank": true,
                "text": "Open in new tab"
            },
            {
                "tag": "span",
                "className": "p-10px"
            },
            {
                "tag": "a",
                "href": "download-link",
                "name": "dashboard.fileinfo.download",
                "text": "Download"
            },
            {
                "tag": "button",
                "name": "dashboard.fileinfo.delete",
                "className": "btn btn-link pdf-delete-link text-danger",
                "text": "Delete"
            }
        ]
    }
];
Template["usersControl"] = [
    {
        "tag": "div",
        "className": "users-control",
        "text": [
            {
                "tag": "div",
                "className": "container",
                "text": {
                    "tag": "table.tbody",
                    "className": "table",
                    "name": "usersControl.data",
                    "text": [
                        {
                            "tag": "tr",
                            "text": [
                                {
                                    "tag": "th",
                                    "text": "S.No."
                                },
                                {
                                    "tag": "th",
                                    "text": "Username"
                                },
                                {
                                    "tag": "th",
                                    "text": "Is Valid"
                                },
                                {
                                    "tag": "th",
                                    "text": "Name"
                                },
                                {
                                    "tag": "th",
                                    "text": "Email"
                                },
                                {
                                    "tag": "th",
                                    "text": "Mobile"
                                },
                                {
                                    "tag": "th",
                                    "text": "Create password otp"
                                },
                                {
                                    "tag": "th",
                                    "text": "Request count"
                                },
                                {
                                    "tag": "th",
                                    "text": "Method"
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    },
    {
        "tag": "div",
        "name": "footer",
        "text": ""
    }
];
Template["usersControl.row"] = [
    {
        "tag": "tr",
        "text": [
            {
                "tag": "td",
                "name": "s.no.",
                "text": ""
            },
            {
                "tag": "td",
                "name": "username",
                "text": ""
            },
            {
                "tag": "td",
                "name": "valid",
                "text": ""
            },
            {
                "tag": "td",
                "name": "name",
                "text": ""
            },
            {
                "tag": "td",
                "name": "email",
                "text": ""
            },
            {
                "tag": "td",
                "name": "mobile",
                "text": ""
            },
            {
                "tag": "td",
                "name": "createPasswordOtp",
                "text": ""
            },
            {
                "tag": "td",
                "name": "methodRequestCount",
                "text": ""
            },
            {
                "tag": "td",
                "name": "method",
                "text": ""
            }
        ]
    }
];
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

Template["footerLinkJson"] = {};
Template["footerLinkJsonAfterLogin"] = {};

export default Template;
