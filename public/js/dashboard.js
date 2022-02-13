$(document).ready(function() {
    
var echartLine = echarts.init(document.getElementById('echart_line'), theme);
      echartLine.setOption({
        title: {
          text: 'Usuarios conectados',
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          x: 220,
          y: 40,
          data: ['Esta semana', 'Semana anterior']
        },
        toolbox: {
          show: true,
          feature: {
            magicType: {
              show: true,
              title: {
                line: 'Line',
                bar: 'Bar',
                stack: 'Stack',
                tiled: 'Tiled'
              },
              type: ['line', 'bar', 'stack', 'tiled']
            },
            restore: {
              show: true,
              title: "Restore"
            },
            saveAsImage: {
              show: true,
              title: "Save Image"
            }
          }
        },
        calculable: true,
        xAxis: [{
          type: 'category',
          boundaryGap: false,
          data: ['Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab', 'Dom']
        }],
        yAxis: [{
          type: 'value'
        }],
        series: [{
          name: 'Esta semana',
          type: 'line',
          smooth: true,
          itemStyle: {
            normal: {
              areaStyle: {
                type: 'default'
              }
            }
          },
          data: [10, 12, 21, 54, 260, 830, 710]
         }, {
          name: 'Semana anterior',
          type: 'line',
          smooth: true,
          itemStyle: {
            normal: {
              areaStyle: {
                type: 'default'
              }
            }
          },
          data: [30, 182, 434, 791, 390, 30, 10]
         }
        ]
      });
      const echarBarHorizElem = document.getElementById('echart_bar_horizontal');
      if (echarBarHorizElem) {
          var echartBar = echarts.init(document.getElementById('echart_bar_horizontal'), theme);
          echartBar.setOption({
            title: {
              text: 'Edad de usuarios',
            },
            tooltip: {
              trigger: 'axis'
            },
            toolbox: {
              show: true,
              feature: {
                saveAsImage: {
                  show: true,
                  title: "Save Image"
                }
              }
            },
            calculable: true,
            xAxis: [{
              type: 'value',
              boundaryGap: [0, 0.01]
            }],
            yAxis: [{
              type: 'category',
              data: ['<20', '21-35', '36-40', '41-50', '>50']
            }],
            series: [{
              type: 'bar',
              data: [25, 60, 40, 20, 5]
            }]
          });
      }
      
      const echartDonutElem = document.getElementById('echart_donut');
      if (echartDonutElem) {
         var echartDonut = echarts.init(echartDonutElem, theme);
          echartDonut.setOption({
            title: {
              text: 'Género de Usuarios',
            },
            tooltip: {
              trigger: 'item',
              formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            calculable: true,
            legend: {
              x: 'center',
              y: 'bottom',
              data: ['Masculino', 'Femenino', 'Otros']
            },
            toolbox: {
              show: true,
              feature: {
                magicType: {
                  show: true,
                  type: ['pie', 'funnel'],
                  option: {
                    funnel: {
                      x: '25%',
                      width: '50%',
                      funnelAlign: 'center',
                      max: 1548
                    }
                  }
                },
                restore: {
                  show: true,
                  title: "Restore"
                },
                saveAsImage: {
                  show: true,
                  title: "Save Image"
                }
              }
            },
            series: [{
              name: 'Usuario',
              type: 'pie',
              radius: ['35%', '55%'],
              itemStyle: {
                normal: {
                  label: {
                    show: true
                  },
                  labelLine: {
                    show: true
                  }
                },
                emphasis: {
                  label: {
                    show: true,
                    position: 'center',
                    textStyle: {
                      fontSize: '14',
                      fontWeight: 'normal'
                    }
                  }
                }
              },
              data: [{
                value: 335,
                name: 'Masculino'
              }, {
                value: 310,
                name: 'Femenino'
              }, {
                value: 15,
                name: 'Otros'
              }]
            }]
          });
      }
     

      window.onresize = function() {
      	echartLine.resize();
      	echartDonut.resize();
      	echartBar.resize();
      };
})