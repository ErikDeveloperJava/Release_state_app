$(document).ready(function () {
    $("#load-more").on("click",function (event) {
        event.preventDefault();
        var page = $("#page").attr("content");
        if(page >= 0){
            loadUsers((parseInt(page) + 1))
        }
    })

    $(document).on("click",".delete-user",function (event) {
        event.preventDefault();
        var id = $(this).attr("id");
        $.ajax({
            type: "POST",
            url: "/user/delete/" + id,
            success: function (isDeleted) {
                if(isDeleted){
                    $("#" + id + "-div").remove();
                    $("#user-blog").empty();
                    loadUsers(0);
                }
            },
            error: function () {

            }
        })
    })
});

var role = $("#role").attr("content");

function loadUsers(page) {
    $.ajax({
        type: "GET",
        url: "/user/load/" + page,
        success: function (users) {
            if(users.length > 0){
                $.each(users,function (i, user) {
                    var userDiv = "<div id='" + user.id + "-div' class='item'>" +
                        "<div class=\"row\">" +
                        "<div class=\"col-md-3\">" +
                        "<div class=\"item-image\">" +
                        "<img src='/users/" + user.picUrl + "' class=\"img-fluid\" alt=\"\"></div>" +
                        "</div>" +
                        "<div class=\"col-md-9\">\n";
                     if(role == "USER"){
                         userDiv+= "<a " +
                             " href='/user/" + user.id +"' class=\"btn btn-primary float-right\">View Profile</a>\n"
                     }else if(role == "ADMIN"){
                         userDiv+="<a " +
                             " id='" + user.id + "' class=\"btn btn-primary float-right delete-user\">Delete</a>\n";
                     }
                     userDiv+=   "<h3 class=\"item-title\">" +
                        "<a href='/user/" + user.id +"' > " + user.name + "</a>" +
                        "</h3>" +
                        "</div>" +
                        "</div>" +
                        "</div>";
                    $("#user-blog").append(userDiv);
                    $("#page").attr("content",page);
                })
            }else {
                $("#page").attr("content","-1");
            }
        },
        error:function () {
            //
        }
    })
}