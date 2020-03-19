using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using SurveyServer.Data;
using SurveyServer.Models;

namespace SurveyServer.Controllers
{
    public class ResponsesController : Controller
    {
        private readonly ApplicationDbContext _context;

        public ResponsesController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: Responses
        public async Task<IActionResult> Index()
        {
            var applicationDbContext = _context.Response.Include(r => r.Survey);
            return View(await applicationDbContext.ToListAsync());
        }

        // GET: Responses/Details/5
        public async Task<IActionResult> Details(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var response = await _context.Response
                .Include(r => r.Survey)
                .FirstOrDefaultAsync(m => m.ResponseID == id);
            if (response == null)
            {
                return NotFound();
            }

            return View(response);
        }

        //// GET: Responses/Create
        //public IActionResult Create()
        //{
        //    ViewData["RefSurveyID"] = new SelectList(_context.Survey, "SurveyID", "SurveyID");
        //    return View();
        //}

        //// POST: Responses/Create
        //// To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        //// more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public async Task<IActionResult> Create([Bind("ResponseID,TimeStamp,Longitude,Latitude,IMEI,RefSurveyID")] Response response)
        //{
        //    if (ModelState.IsValid)
        //    {
        //        _context.Add(response);
        //        await _context.SaveChangesAsync();
        //        return RedirectToAction(nameof(Index));
        //    }
        //    ViewData["RefSurveyID"] = new SelectList(_context.Survey, "SurveyID", "SurveyID", response.RefSurveyID);
        //    return View(response);
        //}

        //// GET: Responses/Edit/5
        //public async Task<IActionResult> Edit(string id)
        //{
        //    if (id == null)
        //    {
        //        return NotFound();
        //    }

        //    var response = await _context.Response.FindAsync(id);
        //    if (response == null)
        //    {
        //        return NotFound();
        //    }
        //    ViewData["RefSurveyID"] = new SelectList(_context.Survey, "SurveyID", "SurveyID", response.RefSurveyID);
        //    return View(response);
        //}

        //// POST: Responses/Edit/5
        //// To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        //// more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public async Task<IActionResult> Edit(string id, [Bind("ResponseID,TimeStamp,Longitude,Latitude,IMEI,RefSurveyID")] Response response)
        //{
        //    if (id != response.ResponseID)
        //    {
        //        return NotFound();
        //    }

        //    if (ModelState.IsValid)
        //    {
        //        try
        //        {
        //            _context.Update(response);
        //            await _context.SaveChangesAsync();
        //        }
        //        catch (DbUpdateConcurrencyException)
        //        {
        //            if (!ResponseExists(response.ResponseID))
        //            {
        //                return NotFound();
        //            }
        //            else
        //            {
        //                throw;
        //            }
        //        }
        //        return RedirectToAction(nameof(Index));
        //    }
        //    ViewData["RefSurveyID"] = new SelectList(_context.Survey, "SurveyID", "SurveyID", response.RefSurveyID);
        //    return View(response);
        //}

        // GET: Responses/Delete/5
        public async Task<IActionResult> Delete(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var response = await _context.Response
                .Include(r => r.Survey)
                .FirstOrDefaultAsync(m => m.ResponseID == id);
            if (response == null)
            {
                return NotFound();
            }

            return View(response);
        }

        // POST: Responses/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(string id)
        {
            var answers =  _context.Answer.Include(r => r.Response).Where(m => m.RefResponseID == id);
            if (answers!=null)
            {
                foreach (Answer answer in answers)
                {
                    _context.Answer.Remove(answer);
                }
                await _context.SaveChangesAsync();
            }
            var response = await _context.Response.FindAsync(id);
            _context.Response.Remove(response);
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }

        private bool ResponseExists(string id)
        {
            return _context.Response.Any(e => e.ResponseID == id);
        }
    }
}
