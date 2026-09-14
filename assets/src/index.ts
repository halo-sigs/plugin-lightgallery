import createGallery from "lightgallery";
import lgZoom from "lightgallery/plugins/zoom";
import "lightgallery/css/lightgallery.css";
import "lightgallery/css/lg-zoom.css";

const instances = new WeakMap<HTMLElement, ReturnType<typeof createGallery>>();

// Preserve the existing theme-side entry point and instance marker.
window.lightGallery = (container, options = {}) => {
  if (!container || container.hasAttribute("lg-uid")) {
    return container ? instances.get(container) : undefined;
  }
  const instance = createGallery(container, { ...options, plugins: [lgZoom] });
  instances.set(container, instance);
  container.setAttribute("lg-uid", "halo-lightgallery-v2");
  return instance;
};
