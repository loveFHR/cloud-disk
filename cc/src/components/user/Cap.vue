<template>
    <div class="chart" ref="chartRef"></div>
</template>
<script lang="ts" setup>
import { onMounted, Ref, ref } from 'vue'
import { ECharts, EChartsOption, init } from 'echarts'
import { $get } from '../../utils/request'

let chart: ECharts;
const chartRef: Ref<HTMLElement | null> = ref(null)
const initChart = async() => {
  let {data} = await $get('/cap/getUserCapInfo')
  const all_cap = data.all_cap/100
  const used_cap = data.used_cap/100
  let ansData = [
    {
      value: all_cap,
      name: '总共',
    },
    {
      value: used_cap,
      name: '已使用',
    }
  ]
  ansData = ansData.filter(item => item.value !== 0);

  const option: EChartsOption = {
    series: [
    {
      type: 'pie',
      stillShowZeroSum: false,
      color: ['rgb(172, 191, 255)','rgb(2, 82, 255)'],
      data: [
        {
          value: all_cap,
          name: '剩余',
        },
        {
          value: used_cap,
          name: '已使用',
        }
      ],
      radius: ['40%', '70%']
    }
  ]
  };
  chart.setOption(option);
}

onMounted(() => {
  chart = init(chartRef.value as HTMLElement)
  initChart()
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 20vh;
  /* background-color: rgb(172, 191, 255); */
}
</style>
