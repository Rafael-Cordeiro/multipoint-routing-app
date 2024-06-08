const routes = [
  {
    path: "",
    component: () => import("layouts/CleanLayout.vue"),
    children: [
      { path: "", component: () => import("pages/LegacyMap.vue"), meta: { title: "Legacy" } },
      { path: "/simplerouting", component: () => import("src/pages/SimpleRoutingMap.vue"), meta: { title: "Simple Routing" } },
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
