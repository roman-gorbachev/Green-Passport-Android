module.exports = [
  {
    id: 'old_clothes',
    questions: {
      ru: 'Как вы обычно избавляетесь от старой одежды?',
      be: 'Як вы звычайна пазбаўляецеся ад старога адзення?',
      en: 'How do you usually get rid of old clothes?',
    },
    optionLists: {
      ru: ['Выбрасываю', 'Отдаю на переработку', 'Отдаю нуждающимся', 'Продаю или меняю'],
      be: ['Выкідваю', 'Аддаю на перапрацоўку', 'Аддаю тым, хто мае патрэбу', 'Прадаю або мяняю'],
      en: ['I throw them away', 'I recycle them', 'I give them to people in need', 'I sell or swap them'],
    },
    isActive: true,
  },
  {
    id: 'commute',
    questions: {
      ru: 'Как вы чаще всего добираетесь на учёбу или работу?',
      be: 'Як вы часцей за ўсё дабіраецеся на вучобу або працу?',
      en: 'How do you usually get to school or work?',
    },
    optionLists: {
      ru: ['Пешком', 'На велосипеде или самокате', 'На общественном транспорте', 'На машине'],
      be: ['Пешшу', 'На ровары або самакаце', 'На грамадскім транспарце', 'На машыне'],
      en: ['On foot', 'By bike or scooter', 'By public transport', 'By car'],
    },
    isActive: false,
  },
  {
    id: 'sorting_habit',
    questions: {
      ru: 'Сортируете ли вы мусор дома?',
      be: 'Ці сартуеце вы смецце дома?',
      en: 'Do you sort your waste at home?',
    },
    optionLists: {
      ru: ['Да, всё', 'Только пластик и бумагу', 'Иногда', 'Пока нет'],
      be: ['Так, усё', 'Толькі пластык і паперу', 'Часам', 'Пакуль не'],
      en: ['Yes, everything', 'Only plastic and paper', 'Sometimes', 'Not yet'],
    },
    isActive: false,
  },
];
