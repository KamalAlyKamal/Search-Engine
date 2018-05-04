var changeTimer = false;

var queries = [];

function isInArray(value, array) {
  return array.indexOf(value) > -1;
}

$('#queryinput').keyup(function () {
    
    if(changeTimer !== false) clearTimeout(changeTimer);
        changeTimer = setTimeout(function(){

            $.ajax({
                type: "GET",
                url: '/WebApplication2/getqueries',
                data: {'query': $('#queryinput').val()},
                success: function(result){
                    // Loop over the JSON array.
                    var dataList = document.getElementById('queries');
                    //clear data list options
                    dataList.innerHTML = '';
                    //append new options from db
                    result.forEach(function(item) {
                        //if(!isInArray(item,queries))
                        //{
                            //queries.push(item);
                            // Create a new <option> element.
                            var option = document.createElement('option');
                            // Set the value using the item in the JSON array.
                            option.value = item;
                            // Add the <option> element to the <datalist>.
                            dataList.appendChild(option);
                        //}
                    }
                    );
                },
                error: function (jqXHR, exception) {
//                  var msg = '';
//                  if (jqXHR.status === 0) {
//                      msg = 'Not connect.\n Verify Network.';
//                  } else if (jqXHR.status === 404) {
//                      msg = 'Requested page not found. [404]';
//                  } else if (jqXHR.status === 500) {
//                      msg = 'Internal Server Error [500].';
//                  } else if (exception === 'parsererror') {
//                      msg = 'Requested JSON parse failed.';
//                  } else if (exception === 'timeout') {
//                      msg = 'Time out error.';
//                  } else if (exception === 'abort') {
//                      msg = 'Ajax request aborted.';
//                  } else {
//                      msg = 'Uncaught Error.\n' + jqXHR.responseText;
//                  }
//                  alert(msg);
              }
              
          //      dataType: dataType
              });
            changeTimer = false;
        },1000);
});

