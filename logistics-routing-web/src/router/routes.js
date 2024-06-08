const routes = [
  {
    path: "",
    component: () => import("layouts/CleanLayout.vue"),
    children: [
      { path: "", component: () => import("pages/LegacyMap.vue") },
      { path: "/simplerouting", component: () => import("src/pages/SimpleRoutingMap.vue") },
    ],
  },


  // Always leave this as last one,
  // but you can also remove it
  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/ErrorNotFound.vue"),
  },
];

export default routes;
