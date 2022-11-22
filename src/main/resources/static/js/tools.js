$(document).ready(function () {
    bacaData();
    // $('#nambah').click(function () {
    //     nambahData();
    // });

    // $('#simpan').click(function () {
    //     editData();
    // });

    $('#batal').click(function () {
        bacaData();
        reset();
    })

    function hapusData(x) {
        $.ajax({
            type: 'POST',
            dataType: 'JSON',
            url: 'php/hapusData.php',
            data: 'id=' + x,
            success: function (response) {
                if (response.status == '1') {
                    alert(response.msg);
                    bacaData();
                } else {
                    alert(response.msg);
                }
            }
        })
    }

    function ambilsatu(x) {
        $.ajax({
            type: 'POST',
            dataType: 'JSON',
            url: 'view.php',
            data: 'id=' + x,
            success: function (response) {
                $('#id').val(response.id);
                $('#files').val(response.files);
                $('#no_tape').val(response.no_tape);
                $('#reporter').val(response.reporter);
                $('#lok_iputan').val(response.lok_liputan);
                $('#deskripsi').val(response.deskripsi);
            }
        })
    }

    // function nambahData() {
    //     var judul = $('#judul').val();
    //     var no_tape = $('#no_tape').val();
    //     var reporter = $('#reporter').val();
    //     var tim_liputan = $('#tim_liputan').val();
    //     var lok_liputan = $('#lok_liputan').val();
    //     var deskripsi = $('#deskripsi').val();

    //     $.ajax({
    //         type: 'POST',
    //         url: 'php/addData.php',
    //         data: 'judul=' + judul + '&tim_liputan=' + tim_liputan + '&reporter=' + reporter + '&lok_liputan=' + lok_liputan + '&deskripsi=' + deskripsi + '&no_tape=' + no_tape + '&files=' + $files,
    //         dataType: 'JSON',
    //         success: function (response) {
    //             if (response.status == '1') {
    //                 alert(response.msg);
    //                 bacaData();

    //             } else {
    //                 alert(response.msg);
    //                 bacaData();

    //             }
    //         }
    //     })
    // }

    function bacaData() {
        $('.tempatdata').html('');
        $.ajax({
            type: 'GET',
            url: '/findAll',
            dataType: 'JSON',
            success: function (response) {
                console.log(response);
                var data = '';
                for (var i = 0; i < response.data.length; i++) {
                    data +=
                        `
                
                <tr>
                <td>`+ (i + 1) + `</td>
                <td>`+ response.data[i].judul + `</td>
                <td>`+ response.data[i].deskripsi + `</td>
                <td>
                
                <a target="_blank" href="view.php?id=`+ response.data[i].id + `"><button type="button" class="btn btn-warning edit">View</button></a>
                <button type="button" class="btn btn-danger del" id=' `+ response.data[i].id + `'>Delete</button>
                </td>
                </tr>
                `
                }
                $('.tempatdata').append(data);
                $('.lihat').click(function () {
                    ambilsatu($(this).attr('id'));
                })
                // $('.lihat').click(function(){
                //     var id=$(this).attr('id');
                //     $.ajax({url:"view.php?id="+id,cache:false,success:function(result){
                //         $(".TEMPATMODAL").html(result);
                //     }});
                // });
                $('.del').click(function () {
                    hapusData($(this).attr('id'));
                })
            }

        })
    }



});
