function render(answers){
        answers.data.forEach(function(data) {
            if (data.type === "textbox" && data.question !== "Edad") {
                return;
            }
        
            let chartDom = document.getElementById(data.id);
            let myChart = echarts.init(chartDom, theme);
            let option;
        
            if (data.type === "radio") {
                let labelValue = data.labels.map(function (e, i) {
                    return {
                        name: e,
                        value: data.values[i]
                    };
                });
        
                option = {
                     title: {
                            display: false,
                            text: data.question,
                            align: 'end'
                    },
                    tooltip: {
                        trigger: 'item'
                    },
                    legend: {
                        orient: 'horizontal',
                        position: 'bottom',
                        data: data.labels
                    
                    },
                    calculable: true,
                    toolbox: {
                        show: true,
                        feature: {
                            dataZoom: {
                                show: true,
                                yAxisIndex: "none"
                            },
                            dataView: {
                                show: true,
                                readOnly: false
                            },
                            restore: {
                                show: true,
                                title: "Restaurar"
                            },
                            saveAsImage: {
                                show: true,
                                title: "Guardar Imágen"
                            }
                        }
                    },
                    series: [
                        {
                            type: 'pie',
                            radius: '50%',
                            data: labelValue,
                        }
                    ]
                };
            } else {
                option = {
                    title: {
                        text: data.question
                    },
                    xAxis: {
                        type: 'category',
                        data: data.labels
                    },
                    yAxis: {
                        type: 'value'
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            dataZoom: {
                                show: true,
                                yAxisIndex: "none"
                            },
                            dataView: {
                                show: true,
                                readOnly: false
                            },
                            restore: {
                                show: true,
                                title: "Restaurar"
                            },
                            saveAsImage: {
                                show: true,
                                title: "Guardar Imágen"
                            }
                        }
                    },
                    series: [
                        {
                            data: data.values,
                            type: 'bar',
                            markPoint: {
                                data: [{
                                    type: 'max',
                                    name: 'Máximo'
                                }, {
                                    type: 'min',
                                    name: 'Mínimo'
                                }]
                            },
                            markLine: {
                                data: [{
                                    type: 'average',
                                    name: 'Promedio'
                                }]
                            }
                        }
                    ]
                };
            }
        
            option && myChart.setOption(option);
        });
    }  

