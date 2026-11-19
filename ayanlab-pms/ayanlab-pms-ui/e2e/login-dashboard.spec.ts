import { test, expect } from "@playwright/test";

async function signInDemoPm(page: import("@playwright/test").Page) {
  await page.goto("/login");
  await page.getByText("AyanLab Property Management").waitFor({ state: "visible" });
  await page.getByRole("button", { name: /pm@demo\.ayanlab/i }).click();
  await expect(page).toHaveURL(/\/app\/dashboard/);
}

test("demo login lands on dashboard", async ({ page }) => {
  await signInDemoPm(page);
  await expect(page.getByRole("heading", { name: "Dashboard" })).toBeVisible();
});

test("commercial terms mock route (post-login)", async ({ page }) => {
  await signInDemoPm(page);
  await page.goto("/app/leasing/commercial");
  await expect(page.getByRole("heading", { name: "Commercial lease terms" })).toBeVisible();
  await expect(page.getByText("Roastery Co.")).toBeVisible();
});