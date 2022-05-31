import { useRailSystemsStore } from "stores/railSystemsStore";

const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/IndexPage.vue')
      },
      {
        path: 'about',
        component: () => import('pages/AboutPage.vue')
      },
      {// Rail Systems page
        path: 'rail-systems/:id',
        component: () => import('pages/RailSystem.vue'),
        props: true
      }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
